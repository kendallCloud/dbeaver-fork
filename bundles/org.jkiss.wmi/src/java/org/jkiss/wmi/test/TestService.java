/*
 * DBeaver - Universal Database Manager
 * Copyright (C) 2010-2023 DBeaver Corp and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jkiss.wmi.test;

import org.jkiss.wmi.service.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * WMI Service tester
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TestService {

    private WMIService service;
    private WMIService nsService;

    public TestService() {
        // Initialize WMIService here if needed
    }

    public static void main(String[] args) {
        new TestService().test();
    }

    void test() {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        try {
            service = WMIService.connect(null, "localhost", null, null, null, "root");

            executor.submit(() -> {
                try {
                    collectClasses();
                } catch (WMIException e) {
                    e.printStackTrace();
                }
            });

            executor.submit(() -> {
                try {
                    testNamespace();
                } catch (WMIException e) {
                    e.printStackTrace();
                }
            });

            executor.shutdown();
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            // Handle interruption
        } finally {
            if (service != null) {
                service.close();
            }
        }

        System.gc();
        System.out.println("DONE");
        System.exit(0);
    }

    private void collectClasses() throws WMIException {
        ObjectCollectorSink classesSink = new ObjectCollectorSink();
        service.enumClasses(null, classesSink, 0);
        classesSink.waitForFinish();
    }

    private void testNamespace() throws WMIException {
        nsService = service.openNamespace("cimv2");
        ObjectCollectorSink tmpSink = new ObjectCollectorSink();
        nsService.executeQuery("SELECT * FROM Win32_Process", tmpSink, WMIConstants.WBEM_FLAG_SEND_STATUS);
        tmpSink.waitForFinish();
        for (WMIObject o : tmpSink.objectList) {
            printProcessInfo(o);
        }
    }

    private void printProcessInfo(WMIObject o) {
        System.out.println("=============");
        System.out.println("Caption=" + o.getValue("Caption"));
        System.out.println("CommandLine=" + o.getValue("CommandLine"));
        System.out.println("CreationClassName=" + o.getValue("CreationClassName"));
        System.out.println("CreationDate=" + o.getValue("CreationDate"));
    }

    private class ObjectCollectorSink implements WMIObjectSink {
        private final List<WMIObject> objectList = Collections.synchronizedList(new ArrayList<>());
        private volatile boolean finished = false;

        @Override
        public void indicate(WMIObject[] objects) {
            Collections.addAll(objectList, objects);
        }

        @Override
        public void setStatus(WMIObjectSinkStatus status, int result, String param, WMIObject errorObject) {
            if (status == WMIObjectSinkStatus.complete) {
                finished = true;
            }
        }

        public void waitForFinish() throws InterruptedException {
            while (!finished) {
                Thread.sleep(100);
            }
        }
    }
}
