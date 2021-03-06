/*
 *
 *  * Copyright 2020 New Relic Corporation. All rights reserved.
 *  * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.newrelic.agent.stats;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Arrays;

import org.json.simple.JSONArray;
import org.junit.Assert;
import org.junit.Test;

import com.newrelic.agent.MetricData;
import com.newrelic.agent.MetricNames;
import com.newrelic.agent.metric.MetricName;
import com.newrelic.agent.transport.DataSenderWriter;

public class MetricDataTest {

    @Test
    public void valid() throws IOException {
        ResponseTimeStatsImpl stats = new ResponseTimeStatsImpl();
        MetricData data = MetricData.create(MetricName.create(MetricNames.EXTERNAL_ALL), stats);

        PrintWriter writer = new PrintWriter(new ByteArrayOutputStream());
        data.writeJSONString(writer);
        writer.close();
    }

    @Test
    public void bad() throws IOException {
        ResponseTimeStatsImpl stats = new ResponseTimeStatsImpl();
        MetricData data = MetricData.create(MetricName.create(MetricNames.EXTERNAL_ALL), stats);

        stats = new ResponseTimeStatsImpl();
        MetricData data2 = MetricData.create(MetricName.create(MetricNames.DISPATCHER), stats);
        stats.setCallCount(-2);

        PrintWriter writer = new PrintWriter(new ByteArrayOutputStream());
        JSONArray.writeJSONString(Arrays.asList(data, data2), writer);
        writer.close();
    }

    @Test
    public void invalidStat() throws IllegalArgumentException, SecurityException, IllegalAccessException,
            NoSuchFieldException {
        StatsImpl stats = new StatsImpl();
        Field countField = AbstractStats.class.getDeclaredField("count");
        countField.setAccessible(true);
        countField.set(stats, -1);
        MetricData data = MetricData.create(MetricName.create("dude"), stats);
        String json = DataSenderWriter.toJSONString(data);
        Assert.assertEquals("[{\"name\":\"dude\"},[0,0,0,0,0,0]]", json);
    }
}
