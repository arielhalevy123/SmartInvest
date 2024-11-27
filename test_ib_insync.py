from ib_insync import IB, Stock

# התחברות ל-Interactive Brokers
ib = IB()
ib.connect('127.0.0.1', 7497, clientId=1)  # ודא שהפורט נכון לפי TWS

# הגדרת המניות
stocks = [
    Stock('AAPL', 'SMART', 'USD'),  # Apple
    Stock('GOOGL', 'SMART', 'USD'),  # Alphabet (Google)
    Stock('TSLA', 'SMART', 'USD')  # Tesla
]

# אישור החוזים מול IB
qualified_stocks = ib.qualifyContracts(*stocks)

# פונקציה שמדפיסה מחירים בזמן אמת
def display_prices():
    tickers = ib.reqTickers(*qualified_stocks)
    for ticker in tickers:
        print(f"Symbol: {ticker.contract.symbol}")
        print(f"Last Price: {ticker.last}")
        print(f"Bid Price: {ticker.bid}")
        print(f"Ask Price: {ticker.ask}")
        print("-" * 30)

# לולאת עדכון נתונים
try:
    while True:
        display_prices()
        ib.sleep(5)  # המתנה של 5 שניות לפני עדכון נוסף
except KeyboardInterrupt:
    print("Stopped real-time price updates.")
finally:
    ib.disconnect()
