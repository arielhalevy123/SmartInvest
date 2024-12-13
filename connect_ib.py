from ib_insync import *

# התחברות ל-IB Gateway
ib = IB()
ib.connect('127.0.0.1', 4001, clientId=1)

# בדיקת מצב החיבור
if ib.isConnected():
    print("החיבור הצליח!")

# דוגמה: בקשת נתוני מניה
stock = Stock('AAPL', 'SMART', 'USD')
ticker = ib.reqMktData(stock)
print(ticker)

# ניתוק
ib.disconnect()
