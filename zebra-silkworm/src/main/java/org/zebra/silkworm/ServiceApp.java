package org.zebra.silkworm;

import org.apache.commons.lang.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;

public class ServiceApp {
    public static void runBean(Class clazz, String... contextsNeeded) {
        Logger logger = LoggerFactory.getLogger(ServiceApp.class.getName());

        logger.info("Initializing the ApplicationContext");
        try {
            SimpleNamingContextBuilder builder = SimpleNamingContextBuilder
                    .emptyActivatedContextBuilder();
            builder.bind("java:comp/env/coreProperties", System.getProperty(
                    "coreProperties", "classpath:zebra.properties"));
        } catch (javax.naming.NamingException e) {
            throw new RuntimeException("Unable to set up mock JNDI context", e);
        }

        // Create the application context
        GenericApplicationContext appContext = new GenericApplicationContext();
        appContext.registerShutdownHook();
        // Load all of the app context XML resources
        XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(
                appContext);
        if (contextsNeeded != null) {
            for (String context : contextsNeeded) {
                logger.debug("Loading context: " + context);
                xmlReader.loadBeanDefinitions(new ClassPathResource(context));
            }
        }

        if (clazz != null) {
            logger.info("Registering bean: " + clazz.getName());
            AnnotatedGenericBeanDefinition beanDef = new AnnotatedGenericBeanDefinition(
                    clazz);
            appContext.registerBeanDefinition(
                    ClassUtils.getShortClassName(clazz), beanDef);
        }

        appContext.refresh();
        logger.info("Done initializing ApplicationContext");

        logger.debug("Entering a sleep loop to let the context(s) run...");
        while (true) {
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }

        logger.info("Closing the ApplicationContext");
        appContext.close();

        logger.debug("Returning from runBean");
    }

    public static void main(String[] args) {
        int exitCode = 0;
        try {
            ServiceApp.runBean(ServiceApp.class, "zebra-context.xml");
        }
        catch (Throwable t) {
            t.printStackTrace();
            exitCode = 1;
        }
        finally {
            Runtime.getRuntime().exit(exitCode);
        }
    }
}
