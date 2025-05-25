package com.kaczmarek.fullstackbackend.service;

import com.kaczmarek.fullstack.generated.model.MessageDto;
import com.kaczmarek.fullstack.generated.model.NewMessageDto;
import com.kaczmarek.fullstackbackend.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

import static org.apache.commons.text.StringEscapeUtils.escapeHtml4;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository repository;

    @Transactional
    @CacheEvict(value = "messages", allEntries = true)
    public MessageDto save(NewMessageDto newMessageDto) {
        var sanitizedContent = escapeHtml4(newMessageDto.getContent());
        var safeDto = new NewMessageDto();
        safeDto.setContent(sanitizedContent);
        log.debug("Saving message with sanitized content: {}", sanitizedContent);
        return repository.save(safeDto);
    }

    @Cacheable(value = "messages")
    public List<MessageDto> getAll() {
        log.debug("Fetching all messages");
        return repository.findAll();
    }
}