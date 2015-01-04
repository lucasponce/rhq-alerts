package org.rhq.alerts.impl;

import org.junit.Test;
import org.rhq.alerts.api.services.DefinitionsService;

import static org.junit.Assert.*;

public class MemDefinitionsServiceImplTest {

    @Test
    public void readInitFilesTest() {
        String path = this.getClass().getClassLoader().getResource(".").getPath();
        System.setProperty("jboss.server.data.dir", path);

        MemDefinitionsServiceImpl memService = new MemDefinitionsServiceImpl();
        memService.init();
        assertTrue(!memService.getRules().isEmpty());
        assertTrue(!memService.getTriggers().isEmpty());
        assertTrue(!memService.getThresholds().isEmpty());
    }
    
}