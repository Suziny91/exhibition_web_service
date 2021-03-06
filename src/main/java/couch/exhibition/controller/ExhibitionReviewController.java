package couch.exhibition.controller;

import couch.exhibition.dto.ReviewRequestDTO;
import couch.exhibition.dto.ReviewResponseDTO;
import couch.exhibition.entity.Exhibition;
import couch.exhibition.entity.Member;
import couch.exhibition.repository.ExhibitionRepository;
import couch.exhibition.service.ExhibitionReviewService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Api(tags = {"Exhibition Review service"})
@Slf4j
@RestController
@RequestMapping("/exhibitions/{exhibitionId}")
public class ExhibitionReviewController {

    private final ExhibitionRepository exhibitionRepository;
    private final ExhibitionReviewService exhibitionReviewService;

    @Autowired
    public ExhibitionReviewController(ExhibitionRepository exhibitionRepository,
                                      ExhibitionReviewService exhibitionReviewService) {
        this.exhibitionRepository = exhibitionRepository;
        this.exhibitionReviewService = exhibitionReviewService;
    }

    @ApiOperation(value = "Exhibition reviews list" , notes = "해당 전시의 리뷰 리스트 조회")
    @GetMapping("/viewAllReviews") // exhibition id에 해당하는 리뷰 조회
    public Page<ReviewResponseDTO> viewExhibitionReviews(@PathVariable("exhibitionId") Long exhibitionId,
                                                         @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Exhibition exhibition = exhibitionRepository.getById(exhibitionId);
        return exhibitionReviewService.getExhibitionReviewList(exhibition, pageable).map(review -> new ReviewResponseDTO(review));
    }

    @ApiOperation(value = "Exhibition review create" , notes = "해당 전시의 리뷰 작성")
    @PostMapping("/reviews") // 리뷰 작성
    public void createExhibitionReview(@PathVariable("exhibitionId") Long exhibitionId,
                                       @RequestBody ReviewRequestDTO createExhibitionReviewDTO,
                                       Authentication authentication) {
        Member member = (Member) authentication.getPrincipal();
        exhibitionReviewService.postReview(member, exhibitionId, createExhibitionReviewDTO);
    }

    @ApiOperation(value = "Exhibition reviews update" , notes = "내가 작성한 리뷰 수정")
    @PatchMapping("/reviews/{reviewId}") // 리뷰 수정
    public void updateExhibitionReview(@PathVariable("exhibitionId") Long exhibitionId,
                                       @PathVariable("reviewId") Long reviewId,
                                       @RequestBody ReviewRequestDTO updateExhibitionReviewDTO,
                                       Authentication authentication) {
        Member member = (Member) authentication.getPrincipal();
        exhibitionReviewService.updateReview(member, exhibitionId, reviewId, updateExhibitionReviewDTO);
    }

    @ApiOperation(value = "Exhibition reviews delete" , notes = "내가 작성한 리뷰 삭제")
    @DeleteMapping("/reviews/{reviewId}") // 리뷰 삭제
    public void deleteExhibitionReview(@PathVariable("exhibitionId") Long exhibitionId,
                                       @PathVariable("reviewId") Long reviewId,
                                       Authentication authentication) {
        Member member = (Member) authentication.getPrincipal();
        exhibitionReviewService.deleteReview(member, exhibitionId, reviewId);
    }
}



