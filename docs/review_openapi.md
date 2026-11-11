# Review hợp đồng OpenAPI – BookStore Catalog API (TH2-1)

Tệp được review: `catalog-service/src/main/resources/static/openapi.yaml` (OpenAPI 3.1.0, version 1.0.0).

## 1. Mã trạng thái đã đúng ngữ nghĩa RFC 9110 chưa?

| Thao tác | Mã | Nhận xét |
|---|---|---|
| `POST /books` | 201 + `Location` + `ETag` | Đúng: tạo tài nguyên mới thì trả 201 kèm `Location` trỏ tới tài nguyên. 409 khi trùng ISBN hợp lý. |
| `GET /books/{isbn}` | 200 / 304 / 404 | Đúng: `If-None-Match` khớp thì trả 304 không kèm thân. |
| `PUT /books/{isbn}` | 200 / 412 / 428 | Đúng: thiếu `If-Match` → 428 Precondition Required; ETag lệch → 412 Precondition Failed. |
| `DELETE /books/{isbn}` | 204 / 404 | Đúng: xoá thành công không có thân thì trả 204. |

Điểm cần cải thiện:

- Phản hồi 304 nên có header `ETag` (RFC 9110 §15.4.5), hiện chưa khai báo.
- `GET`, `PUT`, `DELETE` chưa khai báo 400 cho trường hợp `isbn` ở path sai định dạng `Isbn13`.
- `If-Match` của `PUT` khai báo `required: false` nhưng mô tả lại nói "bắt buộc". Việc để `false` là có chủ ý (để máy chủ tự trả 428 thay vì Spring trả 400), nhưng nên ghi rõ lý do trong mô tả.
- Hai bản `openapi.yaml` (`resources/` và `resources/static/`) có độ dài khác nhau (210 và 268 dòng). Nên giữ một nguồn duy nhất để tránh lệch hợp đồng.

## 2. Ràng buộc `Isbn13`, `Money` có khớp XSD ở TH1 không?

| Ràng buộc | XSD (TH1) | OpenAPI (TH2) | Kết luận |
|---|---|---|---|
| `Isbn13` | pattern `97[89][0-9]{10}` | pattern `^97[89][0-9]{10}$` | Khớp (OpenAPI thêm neo `^ $`, tương đương vì XSD tự khớp toàn chuỗi). |
| Tiền tệ | enum `VND`, `USD` | enum `VND`, `USD` | Khớp. |
| `Money.amount` tối thiểu | `minInclusive 0` | `minimum: 0` | Khớp. |
| `Money.amount` số lẻ | `fractionDigits 2` | `multipleOf: 0.01` | Tương đương về ý nghĩa, nhưng `multipleOf` với số thực JSON dễ sai số làm tròn. |
| Tổng số chữ số | `totalDigits 15` | Không có | Lệch: OpenAPI chưa có cận trên, nên nên thêm `maximum`. |
| `authorId` | `positiveInteger` | `integer`, `minimum: 1` | Khớp. |
| `publishedYear` | `gYear` (không giới hạn) | `minimum: 1450`, `maximum: 2100` | OpenAPI chặt hơn XSD, cần thống nhất. |

## 3. Thao tác nào an toàn / idempotent? Vì sao PUT cần If-Match?

| Thao tác | An toàn (safe) | Idempotent |
|---|---|---|
| GET | Có | Có |
| POST | Không | Không (gọi lại có thể tạo trùng, bị chặn bởi 409 ISBN) |
| PUT | Không | Có (gửi lại cùng nội dung cho cùng kết quả cuối) |
| DELETE | Không | Có (trạng thái cuối như nhau, dù lần sau trả 404) |

`PUT` cần `If-Match` để tránh **lost update**: hai client cùng đọc một phiên bản, client A ghi trước, client B ghi sau sẽ đè mất thay đổi của A mà không biết. Với `If-Match: "<version>"`, nếu phiên bản đã đổi thì máy chủ trả 412 và client B phải đọc lại. Đây là cơ chế khoá lạc quan (optimistic concurrency). Thiếu header thì trả 428 để buộc client luôn gửi điều kiện.

## 4. Thêm trường bắt buộc mới vào `BookCreateRequest` có phá tương thích ngược không?

**Có.** Các client cũ không gửi trường mới nên sẽ nhận 400 (thiếu trường bắt buộc), tức là phá hợp đồng. Cách an toàn:

- Thêm trường dưới dạng **tuỳ chọn**, có giá trị mặc định ở phía máy chủ; hoặc
- Nếu bắt buộc phải có, phát hành phiên bản mới (`/api/v2/books`) và giữ v1 một thời gian.

Ngược lại, thêm trường mới vào **phản hồi** (như `Book`) thường tương thích ngược, miễn là client bỏ qua trường lạ.