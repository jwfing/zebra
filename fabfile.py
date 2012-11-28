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
zebra_silkworm_src_dir='zebra-silkworm'
initd_dir = 'deploy'

def clean_local():
    local("rm -rf %s" % (tmp_dir))

def prepare_spider_local(prod):
    clean_local();

    local("cd %s && mvn clean compile package install -Dmaven.test.skip=true" % (zebra_commons_src_dir))
    local("cd %s && mvn clean compile package dependency:copy-dependencies -Dmaven.test.skip=true" % (zebra_spider_src_dir))

    local("mkdir -p %s/zebra/spider/conf" % (tmp_dir))
    local("mkdir -p %s/zebra/spider/checkpoint" % (tmp_dir))

    local("cp -v %s/target/*.jar %s/zebra/spider" % (zebra_spider_src_dir, tmp_dir))
    local("cp -v %s/src/main/config/%s/* %s/zebra/spider/conf" % (zebra_spider_src_dir, prod, tmp_dir))
    local("cp -rv %s/target/dependency %s/zebra/spider" % (zebra_spider_src_dir, tmp_dir))

def prepare_silkworm_local():
    clean_local();

    local("cd %s && mvn clean compile package install -Dmaven.test.skip=true" % (zebra_commons_src_dir))
    local("cd %s && mvn clean compile package dependency:copy-dependencies -Dmaven.test.skip=true" % (zebra_silkworm_src_dir))

    local("mkdir -p %s/zebra/silkworm/conf" % (tmp_dir))

    local("cp -v %s/target/*.jar %s/zebra/silkworm" % (zebra_silkworm_src_dir, tmp_dir))
    local("cp -rv %s/src/main/config/* %s/zebra/silkworm/conf" % (zebra_silkworm_src_dir, tmp_dir))
    local("cp -rv %s/target/dependency %s/zebra/silkworm" % (zebra_silkworm_src_dir, tmp_dir))

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

def deploy_kestrel():
    prepare_remote_dirs()
    rsync_project(local_dir="third-party/kestrel/",
                  remote_dir=backends_dir + "/kestrel",
                  delete=True)
    put('deploy/kestrel', '/etc/init.d/kestrel', use_sudo=True, mode=0544)
    mkdir_chown("/var/spool/kestrel")
    sudo("chown -R %s:daemon %s" % (user, backends_dir))
    sudo("/etc/init.d/kestrel restart")
    start_on_boot('kestrel', dist)

def deploy_silkworm():
    clean_local()
    prepare_silkworm_local()
    prepare_remote_dirs()
    rsync_project(local_dir=tmp_dir + "/zebra/silkworm/",
            remote_dir=backends_dir + "/zebra-silkworm",
            delete=True)
    sudo("chown -R %s:daemon %s" % (user, backends_dir))
    put('deploy/zebra-silkworm', '/etc/init.d/zebra-silkworm', use_sudo=True, mode=0544)
    clean_local()

def deploy_spider(prod='monkey'):
    clean_local();
    prepare_spider_local(prod);

    prepare_remote_dirs()
    rsync_project(local_dir=tmp_dir + "/zebra/spider/",
                  remote_dir=backends_dir + "/zebra-spider",
                  delete=True)

    sudo("chown -R %s:daemon %s" % (user,backends_dir))
    put('deploy/zebra-spider', '/etc/init.d/zebra-spider',
        use_sudo=True, mode=0544)

    clean_local()

