package edu.hawaii.its.groupings.api;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;

import edu.hawaii.its.holiday.configuration.SpringBootWebApplication;
import edu.internet2.middleware.grouperClient.ws.beans.WsSubjectLookup;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SpringBootWebApplication.class })
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class GroupingsServiceImplTest {

    @Autowired
    private GroupingsServiceImpl groupingsServiceImpl;

    @Test
    public void construction() {
        assertNotNull(groupingsServiceImpl);
    }

    @Test
    public void makeWsSubjectLookup() {
        WsSubjectLookup lookup = groupingsServiceImpl.makeWsSubjectLookup("someuser");
        assertNotNull(lookup);
        assertThat(lookup.getSubjectIdentifier(), equalTo("someuser"));
        assertThat(lookup.getSubjectId(), equalTo(null));
        assertThat(lookup.getSubjectSourceId(), equalTo(null));
    }

}