package org.zebra.common.utils;

import java.util.*;
import org.apache.commons.lang.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;

public class SpringSupport {
    protected Logger logger = LoggerFactory.getLogger(getClass().getName());
    protected ApplicationContext appContext;

    public SpringSupport() {
        this((String[])null);
    }

    public SpringSupport(String ... contextsNeeded) {
        logger.info("Initializing the ApplicationContext");
        // Set up the full list of contexts to be used
        List<String> contexts = new ArrayList<String>();
        if (contextsNeeded != null) {
            for (String context : contextsNeeded) {
                if (!contexts.contains(context)) {
                    contexts.add(context);
                }
            }
        }

        // Now just log which contexts are being included
        for (String context : contexts) {
            logger.debug("Including context: " + context);
        }

        // Create the application context
        appContext = new ClassPathXmlApplicationContext(contexts.toArray(new String[0]));

        // Autowire ourself
        logger.info("Autowiring");
        appContext.getAutowireCapableBeanFactory()
            .autowireBeanProperties(this,
                                    AutowireCapableBeanFactory.AUTOWIRE_BY_NAME,
                                    false);

        logger.info("Done initializing");
    }

    public ApplicationContext getApplicationContext() {
        return appContext;
    }

    public void autowire(Object obj) {
        appContext.getAutowireCapableBeanFactory()
            .autowireBeanProperties(obj,
                                    AutowireCapableBeanFactory.AUTOWIRE_BY_NAME,
                                    false);

    }

    public static void runContexts(String ... contextsNeeded) {
        runBean(null, contextsNeeded);
    }

    public static void runBean(Class clazz,
                               String ... contextsNeeded)
    {
        Logger logger = LoggerFactory.getLogger(SpringSupport.class.getName());

        logger.info("Initializing the ApplicationContext");

        // Create the application context
        GenericApplicationContext appContext = new GenericApplicationContext();
        appContext.registerShutdownHook();
        // Load all of the app context XML resources
        XmlBeanDefinitionReader xmlReader =
            new XmlBeanDefinitionReader(appContext);
        if (contextsNeeded != null) {
            for (String context : contextsNeeded) {
                logger.debug("Loading context: " + context);
                xmlReader.loadBeanDefinitions(new ClassPathResource(context));
            }
        }

        if (clazz != null) {
            logger.info("Registering bean: " + clazz.getName());
            AnnotatedGenericBeanDefinition beanDef =
                new AnnotatedGenericBeanDefinition(clazz);
            appContext.registerBeanDefinition(ClassUtils.getShortClassName(clazz),
                                              beanDef);
        }

        appContext.refresh();
        logger.info("Done initializing ApplicationContext");

        boolean runSleepLoop = true;
        if (clazz != null) {
            Object bean = appContext.getBean(clazz.getName());
            if (bean != null && bean instanceof Runnable) {
                logger.debug("Invoking " + clazz.getName() + ".run()");
                ((Runnable)bean).run();
                logger.debug(clazz.getName() + ".run() returned");
                runSleepLoop = false;
            }
        }

        if (runSleepLoop) {
            logger.debug("Entering a sleep loop to let the context(s) run...");
            while (true) {
                try {
                    Thread.sleep(30000);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        logger.info("Closing the ApplicationContext");
        appContext.close();

        logger.debug("Returning from runBean");
    }
}