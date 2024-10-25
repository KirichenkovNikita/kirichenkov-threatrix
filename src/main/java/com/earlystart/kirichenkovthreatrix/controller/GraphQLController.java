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
}
