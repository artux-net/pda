package net.artux.pda.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneId;

import java.time.Instant;

@Mapper
public interface TimeMapper {

    TimeMapper INSTANCE = Mappers.getMapper(TimeMapper.class);

    default OffsetDateTime to(Instant instant) {
        return OffsetDateTime
                .ofInstant(org.threeten.bp.Instant
                        .ofEpochSecond(instant.getEpochSecond()), ZoneId.systemDefault());
    }

    default Instant to(OffsetDateTime instant) {
        if (instant == null) {
            return Instant.now();
        }
        return Instant.ofEpochSecond(instant.toEpochSecond());
    }
}
