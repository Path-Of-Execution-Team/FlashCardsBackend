package com.flashcards.web.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import jakarta.annotation.PostConstruct;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodIntrospector;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Configuration
public class OpenApiMutationErrorsConfig {

    @Bean
    public OpenAPI addApiErrorSchema() {
        Components c = new Components().addSchemas("ApiError",
            new ObjectSchema()
                .description("Standardowy błąd API")
                .addProperty("timestamp", new StringSchema().format("date-time"))
                .addProperty("status", new IntegerSchema().format("int32"))
                .addProperty("code", new StringSchema())
                .addProperty("message", new StringSchema())
                .addProperty("path", new StringSchema()));
        return new OpenAPI().components(c);
    }

    @Bean
    public Map<Class<? extends Throwable>, HttpStatus> defaultExceptionStatusRegistry() {
        Map<Class<? extends Throwable>, HttpStatus> map = new LinkedHashMap<>();
        map.put(org.springframework.web.bind.MethodArgumentNotValidException.class, HttpStatus.BAD_REQUEST);
        map.put(org.springframework.http.converter.HttpMessageNotReadableException.class, HttpStatus.BAD_REQUEST);
        map.put(Exception.class, HttpStatus.INTERNAL_SERVER_ERROR);
        return map;
    }

    @Bean
    public ExceptionStatusResolver exceptionStatusResolver(
        ListableBeanFactory beanFactory,
        Map<Class<? extends Throwable>, HttpStatus> defaultRegistry
    ) {
        return new ExceptionStatusResolver(beanFactory, defaultRegistry);
    }

    @Bean
    public OperationCustomizer addExceptionResponsesForMutations(ExceptionStatusResolver resolver) {
        final String errorRef = "#/components/schemas/ApiError";
        final LinkedHashMap<Integer, String> statusToDesc = resolver.getAllStatusesAsMap();

        return (operation, hm) -> {
            boolean isPost = hm.getMethod().isAnnotationPresent(PostMapping.class);
            boolean isPut = hm.getMethod().isAnnotationPresent(PutMapping.class);
            boolean isPatch = hm.getMethod().isAnnotationPresent(PatchMapping.class);
            if (!(isPost || isPut || isPatch)) return operation;   // <-- GET NIE DOSTANIE NIC

            ApiResponses responses = operation.getResponses();
            if (responses == null) {
                responses = new ApiResponses();
                operation.setResponses(responses);
            }

            statusToDesc.forEach((code, desc) -> {
                String key = Integer.toString(code);
                ApiResponses response = operation.getResponses();
                if (!response.containsKey(key)) {
                    Content content = new Content().addMediaType("application/json",
                        new MediaType().schema(new Schema<>().$ref(errorRef)));
                    response.addApiResponse(key, new ApiResponse().description(desc).content(content));
                }
            });

            if (isPost && !responses.containsKey("201")) {
                responses.addApiResponse("201", new ApiResponse().description("Created"));
            }
            return operation;
        };
    }

    public static class ExceptionStatusResolver {
        private final ListableBeanFactory beanFactory;
        private final Map<Class<? extends Throwable>, HttpStatus> defaultRegistry;
        private final Map<Class<? extends Throwable>, HttpStatus> resolved = new LinkedHashMap<>();

        public ExceptionStatusResolver(ListableBeanFactory bf, Map<Class<? extends Throwable>, HttpStatus> def) {
            this.beanFactory = bf;
            this.defaultRegistry = def;
        }

        @PostConstruct
        public void init() {
            Map<String, Object> advices = beanFactory.getBeansWithAnnotation(ControllerAdvice.class);
            for (Object advice : advices.values()) {
                Map<Method, ExceptionHandler> handlers = MethodIntrospector.selectMethods(
                    advice.getClass(),
                    (Method m) -> m.isAnnotationPresent(ExceptionHandler.class) ? m.getAnnotation(ExceptionHandler.class) : null
                );
                handlers.forEach((method, ann) -> {
                    Class<? extends Throwable>[] handled = ann.value();
                    if (handled == null || handled.length == 0) return;

                    HttpStatus status = responseStatusOn(method);
                    for (Class<? extends Throwable> exClass : handled) {
                        HttpStatus s = (status != null) ? status : responseStatusOn(exClass);
                        if (s == null) s = lookupDefault(exClass);
                        if (s != null) resolved.putIfAbsent(exClass, s);
                    }
                });
            }
            defaultRegistry.forEach(resolved::putIfAbsent);
        }

        public LinkedHashMap<Integer, String> getAllStatusesAsMap() {
            return resolved.values().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                    HttpStatus::value,
                    HttpStatus::getReasonPhrase,
                    (a, b) -> a,
                    LinkedHashMap::new
                ));
        }

        private HttpStatus responseStatusOn(Method m) {
            ResponseStatus rs = m.getAnnotation(ResponseStatus.class);
            if (rs == null) return null;
            return rs.value() != HttpStatus.INTERNAL_SERVER_ERROR ? rs.value()
                : (rs.code() != HttpStatus.INTERNAL_SERVER_ERROR ? rs.code() : null);
        }

        private HttpStatus responseStatusOn(Class<? extends Throwable> ex) {
            ResponseStatus rs = ex.getAnnotation(ResponseStatus.class);
            if (rs == null) return null;
            return rs.value() != HttpStatus.INTERNAL_SERVER_ERROR ? rs.value()
                : (rs.code() != HttpStatus.INTERNAL_SERVER_ERROR ? rs.code() : null);
        }

        private HttpStatus lookupDefault(Class<? extends Throwable> ex) {
            Class<?> cur = ex;
            while (cur != null && Throwable.class.isAssignableFrom(cur)) {
                HttpStatus st = defaultRegistry.get(cur);
                if (st != null) return st;
                cur = cur.getSuperclass();
            }
            return null;
        }
    }
}
