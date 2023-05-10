package subway.ui;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import subway.application.SectionService;
import subway.application.dto.SectionInsertDto;
import subway.ui.dto.request.SectionRemoveRequest;
import subway.ui.dto.request.SectionRequest;
import subway.ui.query_option.SubwayDirection;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/sections")
public class SectionController {

    private final SectionService sectionService;

    public SectionController(final SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping
    public ResponseEntity<List<Long>> add(@RequestBody SectionRequest request) {
        final List<Long> id = sectionService.save(
                new SectionInsertDto(
                        request.getLineName(),
                        SubwayDirection.from(request.getDirection()),
                        request.getStandardStationName(),
                        request.getAdditionalStationName(),
                        request.getDistance())
        );

        return ResponseEntity.created(URI.create("/sections")).body(id);
    }

    @DeleteMapping
    public ResponseEntity<Void> remove(@RequestBody SectionRemoveRequest sectionRemoveRequest) {
        sectionService.remove(sectionRemoveRequest.getLineId(), sectionRemoveRequest.getStationId());
        return ResponseEntity.noContent().build();
    }

}
