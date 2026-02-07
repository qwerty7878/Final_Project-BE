package com.highpass.runspot.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MultipartJackson2HttpMessageConverter extends AbstractHttpMessageConverter<Object> {

    private final ObjectMapper objectMapper;

    public MultipartJackson2HttpMessageConverter(ObjectMapper objectMapper) {
        super(MediaType.APPLICATION_OCTET_STREAM, MediaType.MULTIPART_FORM_DATA);
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        return objectMapper.readValue(inputMessage.getBody(), clazz);
    }

    @Override
    protected void writeInternal(Object object, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        // Write 기능은 사용하지 않음
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return false;
    }
}