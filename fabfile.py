import os

from datetime import datetime
from fabric.api import run, sudo, env, cd, local, prefix, put, lcd
from fabric.contrib.files import exists
from fabric.contrib.project import rsync_project

user = 'deploy'
backends_dir = '/var/backends'
dist = 'debian'

# local
tmp_dir = '/tmp/backends_tmp'
zebra_commons_src_dir='zebra-commons'
zebra_spider_src_dir='zebra-spider'
initd_dir = 'deploy'

def clean_local():
    local("rm -rf %s" % (tmp_dir))

def prepare_local():
    clean_local();

    local("cd %s && mvn clean compile package install -Dmaven.test.skip=true" % (zebra_commons_src_dir))
    local("cd %s && mvn clean compile package dependency:copy-dependencies -Dmaven.test.skip=true" % (zebra_spider_src_dir))

    local("mkdir -p %s/zebra/spider/conf" % (tmp_dir))
    local("mkdir -p %s/zebra/spider/checkpoint" % (tmp_dir))

    local("cp -v %s/target/*.jar %s/zebra/spider" % (zebra_spider_src_dir, tmp_dir))
    local("cp -v %s/src/main/config/* %s/zebra/spider/conf" % (zebra_spider_src_dir, tmp_dir))
    local("cp -rv %s/target/dependency %s/zebra/spider" % (zebra_spider_src_dir, tmp_dir))

def prepare_remote_dirs():
    if not exists(backends_dir):
        sudo('mkdir -p %s' % backends_dir)
        sudo('chown %s %s' % (user, backends_dir))

def mkdir_chown(dir):
    sudo("mkdir -p %s" % (dir))
    sudo("chown -R %s %s" % (user, dir))

def start_on_boot(name, dist):
    if dist == 'debian':
        sudo('update-rc.d %s defaults' % name)
    elif dist == 'ubuntu':
        sudo('update-rc.d %s defaults' % name)
    elif dist == 'centos':
        sudo('/sbin/chkconfig --level 3 %s on' % name)
    else:
        raise ValueError('dist can only take debian, centos')

def deploy_spider():
    clean_local();
    prepare_local();

    prepare_remote_dirs()
    rsync_project(local_dir=tmp_dir + "/zebra/spider/",
                  remote_dir=backends_dir + "/zebra-spider",
                  delete=True)

    sudo("chown -R %s:daemon %s" % (user,backends_dir))
    put('deploy/zebra-spider', '/etc/init.d/zebra-spider',
        use_sudo=True, mode=0544)

    clean_local()

