package subway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import subway.controller.dto.request.LineRequest;
import subway.controller.dto.response.LineResponse;
import subway.service.LineService;
import subway.service.dto.LineDto;
import subway.service.dto.SectionCreateDto;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final LineService lineService;

    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @PostMapping
    public ResponseEntity<LineResponse> createLine(@RequestBody @Valid LineRequest request) {
        final LineDto lineDto = new LineDto(
                request.getName(),
                request.getColor()
        );
        SectionCreateDto sectionCreateDto = new SectionCreateDto(
                request.getDistance(),
                request.getFirstStation(),
                request.getSecondStation()
        );
        final LineResponse lineResponse = lineService.save(lineDto, sectionCreateDto);
        return ResponseEntity.created(URI.create("/lines/" + lineResponse.getId()))
                .body(lineResponse);
    }

//    @GetMapping
//    public ResponseEntity<List<SingleLineDetailResponse>> readAllLine() {
//        return ResponseEntity.ok(lineService.getAllLine());
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<SingleLineDetailResponse> findLineById(@PathVariable Long id) {
//        return ResponseEntity.ok(lineService.getLine(id));
//    }

//    @PutMapping("/{id}")
//    public ResponseEntity<Void> updateLine(@PathVariable Long id, @RequestBody LineRequest lineUpdateRequest) {
//        lineService.updateLine(id, lineUpdateRequest);
//        return ResponseEntity.ok().build();
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
//        lineService.deleteLineById(id);
//        return ResponseEntity.noContent().build();
//    }

}
