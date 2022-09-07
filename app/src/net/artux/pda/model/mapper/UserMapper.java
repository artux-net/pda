package net.artux.pda.model.mapper;

import net.artux.pda.model.user.FriendRelation;
import net.artux.pda.model.user.Gang;
import net.artux.pda.model.user.GangRelation;
import net.artux.pda.model.user.ProfileModel;
import net.artux.pda.model.user.RegisterUserModel;
import net.artux.pda.model.user.SimpleUserModel;
import net.artux.pda.model.user.UserModel;
import net.artux.pda.ui.fragments.rating.UserInfo;
import net.artux.pdanetwork.model.GangRelationDto;
import net.artux.pdanetwork.model.Profile;
import net.artux.pdanetwork.model.RegisterUserDto;
import net.artux.pdanetwork.model.SimpleUserDto;
import net.artux.pdanetwork.model.UserDto;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = {TimeMapper.class})
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserModel model(UserDto dto);

    UserInfo ratingModel(SimpleUserDto dto);

    List<UserInfo> ratingModels(List<SimpleUserDto> dto);

    ProfileModel model(Profile profile);

    RegisterUserModel model(RegisterUserDto dto);

    RegisterUserDto model(RegisterUserModel model);


    GangRelation relation(GangRelationDto dto);

    default Gang get(UserDto.GangEnum gang) {
        return Enum.valueOf(Gang.class, gang.getValue());
    }

    default Gang get(Profile.GangEnum gang) {
        return Enum.valueOf(Gang.class, gang.getValue());
    }

    default Gang get(SimpleUserDto.GangEnum gang) {
        return Enum.valueOf(Gang.class, gang.getValue());
    }

    default FriendRelation get(Profile.FriendRelationEnum relation) {
        if (relation != null)
            return Enum.valueOf(FriendRelation.class, relation.getValue());
        else return null;
    }

    SimpleUserModel simple(SimpleUserDto dto);

    List<SimpleUserModel> simple(List<SimpleUserDto> dtos);
}
