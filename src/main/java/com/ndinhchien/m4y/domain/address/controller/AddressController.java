package com.ndinhchien.m4y.domain.address.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ndinhchien.m4y.domain.address.dto.AddressRequestDto.AddAddressDto;
import com.ndinhchien.m4y.domain.address.dto.AddressRequestDto.DeleteAddressDto;
import com.ndinhchien.m4y.domain.address.dto.AddressRequestDto.UpdateAddressDto;
import com.ndinhchien.m4y.domain.address.service.AddressService;
import com.ndinhchien.m4y.domain.auth.type.UserDetailsImpl;
import com.ndinhchien.m4y.global.dto.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "address", description = "Address Related APIs")
@RequiredArgsConstructor
@RequestMapping("/api/v1/address")
@RestController
public class AddressController {

    private final AddressService addressService;

    @Operation(summary = "All addresses")
    @GetMapping("/all")
    public BaseResponse<?> getData() {
        return BaseResponse.success("All addresses",
                addressService.getAddresses());
    }

    @Operation(summary = "Add new address")
    @PostMapping
    public BaseResponse<?> addAddress(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid AddAddressDto requestDto) {

        return BaseResponse.success("New address added",
                addressService.addAddress(userDetails.getUser(), requestDto));
    }

    @Operation(summary = "Update address")
    @PutMapping("/sysadmin")
    public BaseResponse<?> updateAddress(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid UpdateAddressDto requestDto) {

        return BaseResponse.success("Update address",
                addressService.sysAdminUpdateAddress(userDetails.getUser(), requestDto));
    }

    @Operation(summary = "Hard delete address")
    @DeleteMapping("/sysadmin")
    public BaseResponse<?> deleteAddress(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid DeleteAddressDto requestDto) {

        return BaseResponse.success("Address deleted count",
                addressService.sysAdminHardDeleteAddress(userDetails.getUser(), requestDto));
    }

}
