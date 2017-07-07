package ch.erard22.cryptoindex.bo


case class ExchangeRate(timestamp: Long, page: String, currency: String, rate: Double) {


  override def toString: String = timestamp + "/" + page + "/" + currency + "/" + rate


}
