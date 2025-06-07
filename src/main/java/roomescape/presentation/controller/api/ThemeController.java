package roomescape.presentation.controller.api;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.business.service.ThemeService;
import roomescape.dto.request.ThemeRegister;
import roomescape.dto.response.ThemeResponse;

@RestController
@RequestMapping("/themes")
@RequiredArgsConstructor
public class ThemeController {

    private final ThemeService themeService;

    @GetMapping
    public List<ThemeResponse> getThemes() {
        return themeService.getAllThemes();
    }

    @GetMapping("/popular")
    public List<ThemeResponse> getPopularThemes(@RequestParam String date) {
        return themeService.findPopularThemes(date);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ThemeResponse addTheme(@RequestBody @Valid ThemeRegister request) {
        return themeService.saveTheme(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTheme(@PathVariable("id") Long id) {
        themeService.deleteTheme(id);
    }
}
