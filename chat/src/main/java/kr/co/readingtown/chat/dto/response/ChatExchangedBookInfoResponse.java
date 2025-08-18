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
                exchangedBookResponse.myBook().exchangeStatusId(),
                exchangedBookResponse.myBook().bookhouseId(),
                myBookInfo.bookName(),
                myBookInfo.bookImage(),
                exchangedBookResponse.myBook().isAccepted()
        );

        ExchangedBookDetail partnerBook = new ExchangedBookDetail(
                exchangedBookResponse.partnerBook().exchangeStatusId(),
                exchangedBookResponse.partnerBook().bookhouseId(),
                partnerBookInfo.bookName(),
                partnerBookInfo.bookImage(),
                exchangedBookResponse.partnerBook().isAccepted()
        );

        return new ChatExchangedBookInfoResponse(myBook, partnerBook);
    }
}
