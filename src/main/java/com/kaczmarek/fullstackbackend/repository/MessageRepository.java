package com.kaczmarek.fullstackbackend.repository;

import com.kaczmarek.fullstack.generated.model.NewMessageDto;
import com.kaczmarek.fullstackbackend.mapper.MessageMapper;
import com.kaczmarek.fullstackbackend.model.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.kaczmarek.fullstack.generated.model.MessageDto;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MessageRepository {

    private final JpaMessageRepository jpaRepository;
    private final MessageMapper mapper;

    public MessageDto save(NewMessageDto dto) {
        var entity = mapper.toEntity(dto);
        entity = jpaRepository.save(entity);
        return mapper.toDto(entity);
    }

    public List<MessageDto> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    interface JpaMessageRepository extends JpaRepository<Message, Long> { }
}