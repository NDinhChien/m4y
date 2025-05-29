package com.ndinhchien.m4y;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ndinhchien.m4y.domain.proposal.entity.Country;
import com.ndinhchien.m4y.domain.proposal.entity.Deanery;
import com.ndinhchien.m4y.domain.proposal.entity.Diocese;
import com.ndinhchien.m4y.domain.proposal.entity.Language;
import com.ndinhchien.m4y.domain.proposal.entity.Parish;
import com.ndinhchien.m4y.domain.proposal.repository.*;
import com.ndinhchien.m4y.domain.user.entity.User;
import com.ndinhchien.m4y.domain.user.repository.UserRepository;
import com.ndinhchien.m4y.domain.user.type.UserRole;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    private final LanguageRepository languageRepository;

    private final CountryRepository countryRepository;

    private final DioceseRepository dioceseRepository;

    private final DeaneryRepository deaneryRepository;

    private final ParishRepository parishRepository;

    private final ObjectMapper objectMapper;

    @Data
    public static class JsonLanguage {
        private String name;
        private String code;
    }

    @Data
    public static class JsonCountry {
        private String name;
        private String code;
        private String languageName;
    }

    public void run(String... args) throws Exception {

        if (userRepository.count() <= 0) {
            User admin = User.builder()
                    .email("admin@email.com")
                    .userName("admin")
                    .password("Aa12345$")
                    .role(UserRole.ADMIN).build();
            admin = saveIfNotExist(admin);

            List<Language> languages = loadLanguages(admin);
            languages = languageRepository.saveAll(languages);
            List<Country> countries = loadCountries(admin);
            countries = countryRepository.saveAll(countries);

            Country vietnam = countryRepository.findByName("Vietnam").orElseThrow();

            Diocese xuanloc = new Diocese(admin, "Xuân Lộc", vietnam);
            xuanloc = dioceseRepository.save(xuanloc);

            Deanery bienhoa = new Deanery(admin, "Biên Hòa", xuanloc);
            bienhoa = deaneryRepository.save(bienhoa);

            Parish donghoa = new Parish(admin, "Đông Hoà", bienhoa, "34/8 Tân Quới, xã Ðông Hòa, Dĩ An, Bình Dương");
            donghoa = parishRepository.save(donghoa);
        }

    }

    private User saveIfNotExist(User user) {
        String email = user.getEmail();
        if (!userRepository.existsByEmail(email)) {
            return userRepository.save(user);
        }
        return userRepository.findByEmail(email).orElse(null);
    }

    public List<Language> loadLanguages(User proposer) {
        try {
            InputStream inputStream = DataInitializer.class
                    .getClassLoader()
                    .getResourceAsStream("data/languages.json");
            JsonLanguage[] languages = objectMapper.readValue(inputStream, JsonLanguage[].class);
            return Arrays.asList(languages).stream().map(json -> {
                return new Language(proposer, json.getName(), json.getCode());
            }).toList();

        } catch (Exception ex) {
            throw new RuntimeException("Failed to load languages init data: " + ex.getMessage());
        }
    }

    public List<Country> loadCountries(User proposer) {
        try {
            InputStream inputStream = DataInitializer.class
                    .getClassLoader()
                    .getResourceAsStream("data/countries.json");
            JsonCountry[] countries = objectMapper.readValue(inputStream, JsonCountry[].class);
            return Arrays.asList(countries).stream().map(json -> {

                Language language = languageRepository.findByName(json.getLanguageName()).orElseThrow(
                        () -> {
                            throw new RuntimeException(
                                    String.format("Language {} does not exist.", json.getLanguageName()));
                        });
                return new Country(proposer, json.getName(), json.getCode(), language);
            }).toList();

        } catch (Exception ex) {
            throw new RuntimeException("Failed to load countries init data: " + ex.getMessage());
        }
    }

}
