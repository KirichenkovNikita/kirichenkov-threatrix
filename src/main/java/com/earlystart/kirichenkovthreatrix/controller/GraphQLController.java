/*
*    ------ BEGIN LICENSE ATTRIBUTION ------
*    
*    Portions of this file have been appropriated or derived from the following project(s) and therefore require attribution to the original licenses and authors.
*    
*    Repository: https://github.com/spring-projects/spring-boot
*    Source File: spring-boot-project/spring-boot-devtools/src/main/java/org/springframework/boot/devtools/livereload/Connection.java
*    Licenses:
*      Apache License 2.0
*      SPDXId: Apache-2.0
*    
*    Auto-attribution by Threatrix, Inc.
*    
*    ------ END LICENSE ATTRIBUTION ------
*/
package com.earlystart.kirichenkovthreatrix.controller;

import com.earlystart.kirichenkovthreatrix.service.user.UserService;
import com.earlystart.kirichenkovthreatrix.service.user.UserServiceImpl;
import graphql.ExecutionInput;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import io.leangen.graphql.GraphQLSchemaGenerator;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class GraphQLController {
    private final GraphQL graphQL;

    @Autowired
    public GraphQLController(UserService userService) {
        GraphQLSchema schema = new GraphQLSchemaGenerator()
                .withOperationsFromSingleton(userService)
                .generate();
        this.graphQL = GraphQL.newGraphQL(schema).build();
    }

    @PostMapping(value = "/graphql", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> indexFromAnnotated(@RequestBody Map<String, String> request, HttpServletRequest raw) {
        var executionResult = graphQL.execute(ExecutionInput.newExecutionInput()
                .query(request.get("query"))
                .operationName(request.get("operationName"))
                .context(raw)
                .build());
        return executionResult.toSpecification();
    }

    private void readWebSocketFrame() throws IOException {
        try {
            Frame frame = Frame.read(this.inputStream);
            if (frame.getType() == Frame.Type.PING) {
                writeWebSocketFrame(new Frame(Frame.Type.PONG));
            }
            else if (frame.getType() == Frame.Type.CLOSE) {
                throw new ConnectionClosedException();
            }
            else if (frame.getType() == Frame.Type.TEXT) {
                logger.debug(LogMessage.format("Received LiveReload text frame %s", frame));
            }
            else {
                throw new IOException("Unexpected Frame Type " + frame.getType());
            }
        }
        catch (SocketTimeoutException ex) {
            writeWebSocketFrame(new Frame(Frame.Type.PING));
            Frame frame = Frame.read(this.inputStream);
            if (frame.getType() != Frame.Type.PONG) {
                throw new IllegalStateException("No Pong");
            }
        }
    }

    /**
     * Trigger livereload for the client using this connection.
     * @throws IOException in case of I/O errors
     */
    void triggerReload() throws IOException {
        if (this.webSocket) {
            logger.debug("Triggering LiveReload");
            writeWebSocketFrame(new Frame("{\"command\":\"reload\",\"path\":\"/\"}"));
        }
    }

    private void complementComponentLicenses(ProjectManifest projectManifest) {
        // fill all components with license data
        List<CompletableFuture<Void>> componentLicenseLookUpTasks = new LinkedList<>();
        AtomicInteger componentsWithNoLicensesCnt = new AtomicInteger();
        projectManifest.getAllComponents().forEach(
                component -> {
                    componentLicenseLookUpTasks.add(
                            CompletableFuture.runAsync(setLicensesToComponentFunction(componentsWithNoLicensesCnt, component), componentLicenseLookupExecutor)
                    );
                }
        );
        componentLicenseLookUpTasks.forEach(CompletableFuture::join);
        logger.info("Components with no licenses: {} of {}", componentsWithNoLicensesCnt.get(), projectManifest.getAllComponents().size());
    }

}
