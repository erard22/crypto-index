package ch.erard22.cryptoindex.pages

/* Alcoion messages */
final case class AlcoinTicker(buy: Double, high: Double, last: Double, low: Double, sell: Double, vol: Double)
final case class AlcoinResponse(date: String, ticker: AlcoinTicker)

/* Bitrex messages */
final case class BitrexResult(Bid: Double, Ask: Double, Last: Double)
final case class BitrexResponse(success: Boolean, message: String, result: BitrexResult)
