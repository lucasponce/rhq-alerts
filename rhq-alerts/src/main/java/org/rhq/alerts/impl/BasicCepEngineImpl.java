package org.rhq.alerts.impl;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.rhq.alerts.cep.CepEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import java.util.Collection;

/**
 TODO
 */
@Singleton
public class BasicCepEngineImpl implements CepEngine {
    private static final Logger LOG = LoggerFactory.getLogger(BasicCepEngineImpl.class);
    private static final String PATH = "src/main/resources/org/poc/rules";

    private KieServices ks;

    private KieRepository kr;
    private KieFileSystem kfs;
    private KieBuilder kb;

    private KieContainer kc;
    private KieSession kSession;


    private String path(String id) {
        return PATH + "/" + id + ".drl";
    }

    public BasicCepEngineImpl() {
        LOG.info("Creating INSTANCE...");
    }

    @PostConstruct
    public void init() {
        initKieArtifacts();
    }
    
    private void initKieArtifacts() {
        ks = KieServices.Factory.get();

        kr = ks.getRepository();
        kfs = ks.newKieFileSystem();
    }

    private boolean initSession() {
        if (kc == null) {
            LOG.warn("No rules detected.");
            return false;
        }
        if (kSession == null) {
            kSession = kc.newKieSession();
        }
        return true;
    }

    @Override
    public void addRule(String id, String rule) {
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null");
        }

        LOG.info("Adding rule " + id + " ...");

        String path = path(id);
        if (kfs.read(path) != null) {
            throw new IllegalArgumentException("Id argument exists on current repository");
        }

        kfs.write(path, rule);

        kb = ks.newKieBuilder(kfs);

        kb.buildAll();
        if (kb.getResults().hasMessages(Message.Level.ERROR)) {
            throw new RuntimeException("Build Errors:\n" + kb.getResults().toString());
        }

        kc = ks.newKieContainer(kr.getDefaultReleaseId());

        if (kSession != null) {
            kSession.dispose();
            kSession = null;
        }
    }

    @Override
    public void removeRule(String id) {
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null");
        }

        LOG.info("Removing rule " + id + " ...");

        String path = path(id);
        if (kfs.read(path) == null) {
            throw new IllegalArgumentException("Id argument does not exist on current repository");
        }

        kfs.delete(path);

        kb = ks.newKieBuilder(kfs);

        kb.buildAll();
        if (kb.getResults().hasMessages(Message.Level.ERROR)) {
            throw new RuntimeException("Build Errors:\n" + kb.getResults().toString());
        }

        kc = ks.newKieContainer(kr.getDefaultReleaseId());

        if (kSession != null) {
            kSession.dispose();
            kSession = null;
        }
    }

    @Override
    public void addGlobal(String name, Object global) {
        if (!initSession()) return;

        LOG.info("Adding global " + name + " - " + global + " ...");

        kSession.setGlobal(name, global);
    }

    @Override
    public void addFact(Object fact) {
        if (!initSession()) return;

        LOG.info("Adding fact " + fact + " ...");

        kSession.insert(fact);
    }

    @Override
    public void addFacts(Collection facts) {
        if (!initSession()) return;

        if (facts != null && !facts.isEmpty()) {
            for (Object fact : facts) {
                LOG.info("Adding fact " + fact + " ...");

                kSession.insert(fact);
            }
        }
    }

    @Override
    public void removeFact(Object fact) {
        if (!initSession()) return;

        FactHandle fh = kSession.getFactHandle(fact);
        if (fh != null) {
            kSession.delete(fh);
        }
    }

    @Override
    public void fire() {
        if (!initSession()) return;

        LOG.info("Firing rules ... ");
        LOG.info("BEFORE Facts: " + kSession.getFactCount());

        kSession.fireAllRules();

        LOG.info("AFTER Facts: " + kSession.getFactCount());
    }

    @Override
    public void clear() {
        if (kSession != null) {
            Collection<FactHandle> facts = kSession.getFactHandles();
            for (FactHandle fact : facts) {
                kSession.delete(fact);
            }
        }
    }

    @Override
    public void reset() {
        kfs = ks.newKieFileSystem();

        if (kSession != null) {
            kSession.dispose();
            kSession = null;
        }
    }
}
