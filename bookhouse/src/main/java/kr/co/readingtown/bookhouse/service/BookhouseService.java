package kr.co.readingtown.bookhouse.service;

import kr.co.readingtown.bookhouse.domain.Bookhouse;
import kr.co.readingtown.bookhouse.domain.enums.IsExchanged;
import kr.co.readingtown.bookhouse.dto.request.BookInfoRequestDto;
import kr.co.readingtown.bookhouse.dto.response.BookPreviewResponseDto;
import kr.co.readingtown.bookhouse.dto.response.BookhouseOwnerResponseDto;
import kr.co.readingtown.bookhouse.dto.response.BookhouseSearchResponseDto;
import kr.co.readingtown.bookhouse.dto.response.ExchangeStatusResponse;
import kr.co.readingtown.bookhouse.dto.response.ExchangingBookDetail;
import kr.co.readingtown.bookhouse.dto.response.ExchangingBookResponse;
import kr.co.readingtown.bookhouse.exception.BookhouseException;
import kr.co.readingtown.bookhouse.integration.book.BookReader;
import kr.co.readingtown.bookhouse.dto.response.MemberProfileResponseDto;
import kr.co.readingtown.bookhouse.integration.member.MemberReader;
import kr.co.readingtown.bookhouse.repository.BookhouseRepository;
import kr.co.readingtown.common.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookhouseService {

    private final BookReader bookReader;
    private final MemberReader memberReader;
    private final BookhouseRepository bookhouseRepository;

    // 서재에 책 등록
    @Transactional
    public void addBooksToBookhouse(Long memberId, BookInfoRequestDto bookInfoRequestDto) {

        Long bookId = bookReader.saveIfNotExistsAndGetBookId(bookInfoRequestDto);

        Bookhouse newBookhouse = Bookhouse.builder()
                .memberId(memberId)
                .bookId(bookId)
                .isExchanged(IsExchanged.PENDING)
                .build();
        bookhouseRepository.save(newBookhouse);
    }

    // 서재에 책 등록 (bookId만 사용)
    @Transactional
    public void addBooksToBookhouseByBookId(Long memberId, Long bookId) {
        
        // 책이 존재하는지 확인
        if (!bookReader.existsBook(bookId)) {
            throw new BookhouseException.BookNotFound();
        }

        // 이미 등록된 책인지 확인
        if (bookhouseRepository.existsByMemberIdAndBookId(memberId, bookId)) {
            throw new BookhouseException.BookAlreadyExists();
        }

        Bookhouse newBookhouse = Bookhouse.builder()
                .memberId(memberId)
                .bookId(bookId)
                .isExchanged(IsExchanged.PENDING)
                .build();
        bookhouseRepository.save(newBookhouse);
    }

    // 서재에서 책 삭제
    @Transactional
    public void deleteBookFromBookhouse(Long memberId, Long bookId) {

        Bookhouse bookhouse = bookhouseRepository.findByMemberIdAndBookId(memberId, bookId)
                .orElseThrow(BookhouseException.BookhouseNotFound::new);
        bookhouseRepository.delete(bookhouse);
    }

    // 특정 회원의 서재 책 리스트 조회
    public PageResponse<BookPreviewResponseDto> getBookhouseBooks(Long memberId, int page, int size) {

        Page<Bookhouse> bookhousePage = bookhouseRepository.findAllByMemberIdOrderByCreatedAtDesc(memberId, PageRequest.of(page, size));

        List<Long> bookIds = bookhousePage.stream()
                .map(Bookhouse::getBookId)
                .toList();

        List<BookPreviewResponseDto> bookInfoList = bookReader.getBookInfo(bookIds);
        
        // bookId를 키로 하는 Map 생성
        Map<Long, BookPreviewResponseDto> bookInfoMap = bookInfoList.stream()
                .collect(Collectors.toMap(BookPreviewResponseDto::bookId, Function.identity()));
        
        // bookhouseId를 포함한 새로운 리스트 생성
        List<BookPreviewResponseDto> content = new ArrayList<>();
        List<Bookhouse> bookhouseList = bookhousePage.getContent();
        
        for (Bookhouse bookhouse : bookhouseList) {
            BookPreviewResponseDto bookInfo = bookInfoMap.get(bookhouse.getBookId());
            
            content.add(new BookPreviewResponseDto(
                    bookhouse.getBookhouseId(),
                    bookInfo.bookId(),
                    bookInfo.bookImage(),
                    bookInfo.bookName(),
                    bookInfo.author()
            ));
        }

        return PageResponse.of(content, bookhousePage);
    }

    // 교환 중인 책 리스트 조회
    public List<ExchangingBookResponse> getMyExchangingBooks(Long memberId) {
        
        // EXCHANGED 상태인 내 책들 조회
        List<Bookhouse> myExchangedBooks = bookhouseRepository.findByMemberIdAndIsExchangedOrderByCreatedAtDesc(memberId, IsExchanged.EXCHANGED);
        
        if (myExchangedBooks.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 채팅방별로 그룹화 (채팅방당 내 책은 1개)
        Map<Long, Bookhouse> booksByChatroom = myExchangedBooks.stream()
                .filter(b -> b.getChatroomId() != null)
                .collect(Collectors.toMap(
                        Bookhouse::getChatroomId,
                        book -> book,
                        (existing, replacement) -> existing // 중복 시 기존 것 유지
                ));

        List<ExchangingBookResponse> responses = new ArrayList<>();

        for (Map.Entry<Long, Bookhouse> entry : booksByChatroom.entrySet()) {
            Long chatroomId = entry.getKey();
            Bookhouse myBook = entry.getValue();
            
            // 같은 채팅방의 상대방 책 찾기 (채팅방에는 2개의 책만 존재)
            List<Bookhouse> ChatroomBooks = bookhouseRepository.findAllByChatroomId(chatroomId);
            Bookhouse partnerBook = ChatroomBooks.stream()
                    .filter(b -> !b.getMemberId().equals(memberId))
                    .findFirst()
                    .orElse(null);
            
            // 책 정보 조회
            BookPreviewResponseDto myBookInfo = bookReader.getBookInfo(List.of(myBook.getBookId())).get(0);
            BookPreviewResponseDto partnerBookInfo = null;
            
            if (partnerBook != null) {
                partnerBookInfo = bookReader.getBookInfo(List.of(partnerBook.getBookId())).get(0);
            }
            
            // Response 생성
            ExchangingBookDetail myBookDetail = new ExchangingBookDetail(
                    myBook.getBookhouseId(),
                    myBookInfo.bookName(),
                    myBookInfo.bookImage()
            );
            
            ExchangingBookDetail yourBookDetail = partnerBookInfo != null 
                    ? new ExchangingBookDetail(
                        partnerBook.getBookhouseId(),
                        partnerBookInfo.bookName(),
                        partnerBookInfo.bookImage()
                    )
                    : new ExchangingBookDetail(null, null, null);
            
            responses.add(new ExchangingBookResponse(chatroomId, myBookDetail, yourBookDetail));
        }

        return responses;
    }

    // 키워드로 Bookhouse에 등록된 책 검색
    public List<BookhouseSearchResponseDto> searchBooksInBookhouse(String keyword, Long currentMemberId) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        
        // 검색 키워드에서 띄어쓰기 제거
        String normalizedKeyword = keyword.trim().replace(" ", "");
        return bookhouseRepository.searchBooksInBookhouse(normalizedKeyword, currentMemberId);
    }

    // 특정 책을 가진 서재 리스트 조회
    public List<BookhouseOwnerResponseDto> getBookhousesByBookId(Long bookId, Long currentMemberId) {
        List<Bookhouse> bookhouses = bookhouseRepository.findAllByBookIdOrderByCreatedAtDesc(bookId);

        // 로그인한 사용자 본인은 제외
        if (currentMemberId != null) {
            bookhouses = bookhouses.stream()
                    .filter(bh -> !bh.getMemberId().equals(currentMemberId))
                    .toList();
        }

        if (bookhouses.isEmpty()) {
            return List.of();
        }

        List<Long> memberIds = bookhouses.stream()
                .map(Bookhouse::getMemberId)
                .distinct()
                .toList();

        // 멤버 프로필 정보 한번에 조회 (이름, 프로필 이미지, 별점)
        Map<Long, MemberProfileResponseDto> memberProfiles = memberReader.getMembersProfile(memberIds);

        // 팔로우 여부 조회 (로그인한 사용자가 있을 경우)
        Map<Long, Boolean> followingMap = new HashMap<>();
        if (currentMemberId != null) {
            followingMap = memberReader.checkFollowing(currentMemberId, memberIds);
        }

        List<BookhouseOwnerResponseDto> responses = new ArrayList<>();
        for (Bookhouse bookhouse : bookhouses) {
            Long memberId = bookhouse.getMemberId();
            MemberProfileResponseDto profile = memberProfiles.get(memberId);

            if (profile == null) {
                profile = new MemberProfileResponseDto("알 수 없음", null, 0.0);
            }

            responses.add(new BookhouseOwnerResponseDto(
                    bookhouse.getBookhouseId(),
                    memberId,
                    profile.nickname(),
                    profile.profileImage(),
                    followingMap.getOrDefault(memberId, false),
                    profile.starRating() != null ? profile.starRating() : 0.0
            ));
        }

        return responses;
    }

    // 유저가 서재에 가지고있는 책의 id 조회
    public List<Long> getMembersBookId(Long memberId) {
        return bookhouseRepository.findBookIdByMember(memberId);
    }

    // 채팅방의 현재 교환 상태 조회
    public ExchangeStatusResponse getExchangeStatusForChatroom(Long chatroomId) {
        // 이 채팅방에서 현재 교환 중인 Bookhouse 조회 (chatroomId가 일치하는 것만)
        List<Bookhouse> books = bookhouseRepository.findAllByChatroomId(chatroomId);

        if (books.isEmpty()) {
            return null;  // 이 채팅방에서 현재 교환 중인 책이 없음
        }

        // 첫 번째 책의 IsExchanged 상태 반환 (두 책은 항상 동일한 상태여야 함)
        return new ExchangeStatusResponse(books.get(0).getIsExchanged().name());
    }
}
