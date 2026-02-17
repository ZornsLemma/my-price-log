# My Price Log

An Android app to track and compare prices offline. Please see the [user manual](https://zornslemma.github.io/my-price-log-docs/) for an overview of the app and how to use it.

## NFC-e QR Import (SP)

The app now supports importing São Paulo NFC-e receipts from QR codes.

Pipeline:
1. Open **Import NFC-e QR (SP)** from the home screen menu.
2. Scan the QR code using CameraX + ML Kit.
3. Resolve and normalize the SP NFC-e URL.
4. Fetch receipt HTML using OkHttp.
5. Parse receipt fields and line items using Jsoup with regex fallback.
6. Map parsed content into `source`, `item`, `price`, and `price_history` in one Room transaction.
7. Record dedupe key in `nfce_import` to prevent double import.

Limitations:
- Parsing depends on public NFC-e page structure and may skip malformed items.
- If a receipt lacks an access key, dedupe uses SHA-256 of source URL.
- Import is automatic and intentionally skips manual confirmation.
