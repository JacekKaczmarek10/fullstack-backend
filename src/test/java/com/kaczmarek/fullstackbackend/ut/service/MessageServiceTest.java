package com.kaczmarek.fullstackbackend.ut.service;

import com.kaczmarek.fullstack.generated.model.MessageDto;
import com.kaczmarek.fullstack.generated.model.NewMessageDto;
import com.kaczmarek.fullstackbackend.repository.MessageRepository;
import com.kaczmarek.fullstackbackend.service.MessageService;
import org.apache.commons.text.StringEscapeUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.kaczmarek.fullstackbackend.TestDataFactory.createMessageDto;
import static com.kaczmarek.fullstackbackend.TestDataFactory.createNewMessageDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageRepository repository;

    @InjectMocks
    private MessageService service;

    @Nested
    class SaveTest {

        @Test
        void shouldEscapeHtmlContent() {
            final var newMessageDto = createNewMessageDto("<b>bold</b>");
            final var escapedContent = StringEscapeUtils.escapeHtml4(newMessageDto.getContent());
            final var savedDto = createMessageDto(1L, escapedContent);

            when(repository.save(any(NewMessageDto.class))).thenReturn(savedDto);
            final var result = service.save(newMessageDto);

            verify(repository).save(argThat(dto -> escapedContent.equals(dto.getContent())));
            assertThat(result).isNotNull();
            assertThat(result.getContent()).isEqualTo(escapedContent);
        }

        @Test
        void shouldEscapeHtmlContent_WhenScriptTag() {
            final var newMessageDto = createNewMessageDto("<script>alert('xss')</script>");
            final var escapedContent = StringEscapeUtils.escapeHtml4(newMessageDto.getContent());
            final var savedDto = createMessageDto(2L, escapedContent);

            when(repository.save(any(NewMessageDto.class))).thenReturn(savedDto);
            final var result = service.save(newMessageDto);

            verify(repository).save(argThat(dto -> escapedContent.equals(dto.getContent())));
            assertThat(result.getContent()).contains("&lt;script&gt;");
        }
    }

    @Nested
    class GetAllTest {

        @Test
        void shouldReturnEmptyList_WhenNoMessages() {
            when(repository.findAll()).thenReturn(List.of());

            final var result = service.getAll();

            assertThat(result).isEmpty();
        }

        @Test
        void shouldReturnListOfMessageDtos() {
            final var dtos = List.of(
                createMessageDto(1L, "Hello"),
                createMessageDto(2L, "World")
            );

            when(repository.findAll()).thenReturn(dtos);
            final var result = service.getAll();

            assertThat(result).hasSize(2);
            assertThat(result).extracting(MessageDto::getContent).containsExactly("Hello", "World");
        }
    }
}