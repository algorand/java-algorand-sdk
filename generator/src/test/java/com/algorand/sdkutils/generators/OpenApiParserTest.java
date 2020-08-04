package com.algorand.sdkutils.generators;

import com.algorand.sdkutils.listeners.Publisher;
import com.algorand.sdkutils.listeners.Subscriber;
import com.algorand.sdkutils.utils.QueryDef;
import com.algorand.sdkutils.utils.StructDef;
import com.algorand.sdkutils.utils.TypeDef;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableList;
import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class OpenApiParserTest {
    private InputStream getInputStreamForResource(String resource) {
        ClassLoader loader = getClass().getClassLoader();
        return loader.getResourceAsStream(resource);
    }


    @Test
    public void basicTest() throws Exception {
        // Given - mock subscriber listening to all events while parsing "petstore.json"
        Subscriber subscriber = mock(Subscriber.class);
        InputStream istream = getInputStreamForResource("petstore.json");
        Publisher publisher = new Publisher();
        publisher.subscribeAll(subscriber);

        // When - we receive events
        JsonNode node = Utils.getRoot(istream);
        OpenApiParser parser = new OpenApiParser(node, publisher);
        parser.parse();

        // Then - verify that some of the expected events

        // 'onEvent(evt, StructDef)` is called as expected
        List<String> expectedStructs = ImmutableList.of("ApiResponse", "Category", "Pet", "Tag", "Order", "User");
        ArgumentCaptor<StructDef> structDefArgumentCaptor = ArgumentCaptor.forClass(StructDef.class);
        verify(subscriber, times(expectedStructs.size()))
                .onEvent(any(), structDefArgumentCaptor.capture());

        assertThat(structDefArgumentCaptor.getAllValues())
                .extracting("name")
                .containsAll(expectedStructs);

        // 'onEvent(evt)' - called 14 times, once for each END_QUERY.
        verify(subscriber, times(14))
                .onEvent(any());

        // 'onEvent(evt, []String)` - called 14 times, once for each NEW_QUERY.
        verify(subscriber, times(14))
                .onEvent(any(), any(QueryDef.class));

        // Property, Path param, Query param, Body
        verify(subscriber, times(42))
                .onEvent(any(), any(TypeDef.class));
    }
}