package kr.co.readingtown.chat.dto.response;

public record ChatExchangedBookInfoResponse(

        ExchangedBookDetail myBook,
        ExchangedBookDetail partnerBook
) {

    public static ChatExchangedBookInfoResponse from(
            ExchangedBookResponse exchangedBookResponse,
            BookInfoResponse myBookInfo,
            BookInfoResponse partnerBookInfo
    ) {
        ExchangedBookDetail myBook = new ExchangedBookDetail(
                exchangedBookResponse.myBook() != null ? exchangedBookResponse.myBook().exchangeStatusId() : null,
                exchangedBookResponse.myBook() != null ? exchangedBookResponse.myBook().bookhouseId() : null,
                myBookInfo != null ? myBookInfo.bookName() : null,
                myBookInfo != null ? myBookInfo.bookImage() : null,
                exchangedBookResponse.myBook() != null ? exchangedBookResponse.myBook().isAccepted() : null
        );

        ExchangedBookDetail partnerBook = new ExchangedBookDetail(
                exchangedBookResponse.partnerBook() != null ? exchangedBookResponse.partnerBook().exchangeStatusId() : null,
                exchangedBookResponse.partnerBook() != null ? exchangedBookResponse.partnerBook().bookhouseId() : null,
                partnerBookInfo != null ? partnerBookInfo.bookName() : null,
                partnerBookInfo != null ? partnerBookInfo.bookImage() : null,
                exchangedBookResponse.partnerBook() != null ? exchangedBookResponse.partnerBook().isAccepted() : null
        );

        return new ChatExchangedBookInfoResponse(myBook, partnerBook);
    }
}
