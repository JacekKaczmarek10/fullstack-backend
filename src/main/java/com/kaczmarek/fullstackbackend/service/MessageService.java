package com.kaczmarek.fullstackbackend.service;

import com.kaczmarek.fullstack.generated.model.MessageDto;
import com.kaczmarek.fullstack.generated.model.NewMessageDto;
import com.kaczmarek.fullstackbackend.mapper.MessageMapper;
import com.kaczmarek.fullstackbackend.model.Message;
import com.kaczmarek.fullstackbackend.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageMapper mapper;
    private final MessageRepository repository;

    public MessageDto save(NewMessageDto newMessageDto) {
        final var sanitizedContent = sanitizeContent(newMessageDto.getContent());
        logMessage(sanitizedContent);
        final var savedMessage = saveMessage(sanitizedContent);
        return convertToDto(savedMessage);
    }

    private String sanitizeContent(String content) {
        return StringEscapeUtils.escapeHtml4(content);
    }

    private void logMessage(String content) {
        log.info("Saving message: {}", content);
    }

    private Message saveMessage(String content) {
        var message = new Message();
        message.setContent(content);
        return repository.save(message);
    }

    private MessageDto convertToDto(Message message) {
        return mapper.toDto(message);
    }

    public List<MessageDto> getAll() {
        logFetchingAllMessages();
        final var messages = repository.findAll();
        return convertToDtoList(messages);
    }

    private void logFetchingAllMessages() {
        log.debug("Fetching all messages");
    }

    private List<MessageDto> convertToDtoList(List<Message> messages) {
        return mapper.toDtoList(messages);
    }

}
