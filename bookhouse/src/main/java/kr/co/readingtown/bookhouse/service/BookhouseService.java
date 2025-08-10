package kr.co.readingtown.bookhouse.service;

import kr.co.readingtown.bookhouse.domain.Bookhouse;
import kr.co.readingtown.bookhouse.exception.BookhouseException;
import kr.co.readingtown.bookhouse.repository.BookhouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookhouseService {

    private final BookhouseRepository bookhouseRepository;

    public void registerBookhouse(Long memberId) {
        // 이미 존재하면 생성 실패 처리
        if (bookhouseRepository.existsByMemberId(memberId)) {
            throw new BookhouseException.BookhouseCreateFailed();
        }

        Bookhouse newBookhouse = new Bookhouse(memberId);
        bookhouseRepository.save(newBookhouse);

        // 저장 직후에도 보장 검증
        if (!bookhouseRepository.existsByMemberId(memberId)) {
            throw new BookhouseException.BookhouseCreateFailed();
        }
    }

    public Long getBookhouseId(Long memberId) {
        return bookhouseRepository.findBookhouseIdByMemberId(memberId)
                .orElseThrow(BookhouseException.BookhouseNotFound::new);
    }
}
