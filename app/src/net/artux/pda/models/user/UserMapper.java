package net.artux.pda.models.user;

import net.artux.pda.generated.models.GangRelationDto;
import net.artux.pda.generated.models.Profile;
import net.artux.pda.generated.models.UserDto;
import net.artux.pda.generated.models.UserEntity;
import net.artux.pda.generated.models.UserInfoDto;
import net.artux.pda.ui.fragments.rating.UserInfo;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserModel dto(UserDto dto);

    UserInfo ratingModel(UserInfoDto dto);

    List<UserInfo> ratingModels(List<UserInfoDto> dto);

    ProfileModel model(Profile profile);


    GangRelation relation(GangRelationDto dto);

    default Gang get(UserDto.Gang gang) {
        return Enum.valueOf(Gang.class, gang.getValue());
    }

    default Gang get(UserEntity.Gang gang) {
        return Enum.valueOf(Gang.class, gang.getValue());
    }

    default Gang get(Profile.Gang gang) {
        return Enum.valueOf(Gang.class, gang.getValue());
    }

    default FriendRelation get(Profile.FriendRelation relation) {
        if (relation != null)
            return Enum.valueOf(FriendRelation.class, relation.getValue());
        else return null;
    }

}
