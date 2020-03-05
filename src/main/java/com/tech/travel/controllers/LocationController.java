package com.tech.travel.controllers;

import com.tech.travel.api.responses.BaseResponse;
import com.tech.travel.api.responses.ErrorResponse;
import com.tech.travel.api.responses.LocationResponse;
import com.tech.travel.api.responses.ListLocationResponse;
import com.tech.travel.models.Location;
import com.tech.travel.models.Translation;
import com.tech.travel.repository.TranslationRepository;
import javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


@RestController
@RequestMapping("/travel")
public class LocationController {

    private final Logger log =  LoggerFactory.getLogger(LocationController.class);
    @Autowired
    private final TranslationRepository translationRepo;

    public LocationController(TranslationRepository translationRepo) {
        this.translationRepo = translationRepo;
    }

    @GetMapping("/locations")
    @PreAuthorize("hasRole('USER') or hasRole('OPS') or hasRole('ADMIN')")
    public ResponseEntity getLocations(HttpServletRequest request,
                                       @RequestHeader(value = "accept-language") String language) {
        String transactionId = request.getHeader("X-Transaction-Id");
        logInfoWithTransactionId(
                transactionId,
                "Got request to fetch list of locations"
        );
        try {
            language = getFormattedLanguage(language);
            logInfoWithTransactionId(
                    transactionId,
                    String.format("performing request on language: %s", language)
            );
            List<Translation> translations = translationRepo.findAllLocationsByTLanguage(language);
            logInfoWithTransactionId(
                    transactionId,
                    String.format("found %s location(s)", translations.size())
            );

            List<LocationResponse> listLocations = new ArrayList<>();
            for(Translation t : translations) {
                LocationResponse lr = generateLocationResponse(t);
                listLocations.add(lr);
            }
            ListLocationResponse response = new ListLocationResponse(listLocations);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error(String.format("[LOCATIONS] %s: error %s", transactionId, e.getMessage()));
            return generateErrorResponse(500, "internal server error", transactionId);
        }
    }

    @GetMapping("/locations/{type}/{code}")
    @PreAuthorize("hasRole('USER') or hasRole('OPS') or hasRole('ADMIN')")
    public ResponseEntity getLocationsByTypeAndCode(
            HttpServletRequest request,
            @PathVariable("type") @NotEmpty @NotNull Location.LocationType type,
            @PathVariable("code") @NotEmpty @NotNull String code,
            @RequestHeader(value = "accept-language") String language) {
        String transactionId = request.getHeader("X-Transaction-Id");
        logInfoWithTransactionId(
                transactionId,
                "Got request to fetch a location by type and code"
        );
        try {
            language = getFormattedLanguage(language);
            Translation translation = translationRepo.findLocationByLanguageTypeAndCode(language, type, code);
            if(translation == null)
                throw new NotFoundException("data not found");
            logInfoWithTransactionId(
                    transactionId,
                    String.format("found %s location(s)", translation.getLocation())
            );
           LocationResponse response = generateLocationResponse(translation);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (NotFoundException e) {
            log.error(String.format("[LOCATIONS] %s: error %s", transactionId, e.getMessage()));
            return generateErrorResponse(404, "data not found", transactionId);
        } catch (Exception e) {
            log.error(String.format("[LOCATIONS] %s: error %s", transactionId, e.getMessage()));
            return generateErrorResponse(500, "internal server error", transactionId);
        }
    }

    private String getFormattedLanguage(String language) {
        Pattern languagePattern = Pattern.compile("^([a-z]{2}[-][A-Z]{2})|[A-Z]{2}");
        if(languagePattern.matcher(language).matches()) {
            String[] lang = language.split("-");
            return lang[0].toUpperCase();
        }
        return "EN";
    }

    private LocationResponse generateLocationResponse(Translation t) {
        LocationResponse lr = new LocationResponse();
        lr.setCode(t.getFkLocation().getCode());
        lr.setName(t.getName());
        lr.setType(t.getFkLocation().getType());
        lr.setLongitude(t.getFkLocation().getLongitude());
        lr.setLatitude(t.getFkLocation().getLatitude());
        lr.setDescription(t.getDescription());
        if(t.getFkLocation().getParentId() != null) {
            lr.setParentCode(t.getFkLocation().getParentId());
            lr.setParentType(t.getFkLocation().getLoc().getType());
        }
        return lr;
    }

    private void logInfoWithTransactionId(String transactionId, String message) {
        log.info(String.format("[LOCATIONS] %s: %s", transactionId, message));
    }

    private void logErrorWithTransactionId(String transactionId, String message) {
        log.error(String.format("[FILTER] %s: %s", transactionId, message));
    }

    private ResponseEntity generateErrorResponse(int statusCode, String message, String transactionId) {
        BaseResponse meta = new BaseResponse(transactionId, "ERROR", message, statusCode);
        ErrorResponse response = new ErrorResponse(meta);
        return ResponseEntity.status(statusCode).body(response);
    }

}
