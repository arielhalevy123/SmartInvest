from ib_insync import IB, Stock
import asyncio
from datetime import datetime

from ib_insync import IB, Stock, MarketOrder
import asyncio

from ib_insync import IB, Stock, MarketOrder
import asyncio
from datetime import datetime
from ib_insync import IB, Stock, MarketOrder
import asyncio

async def buy_stock(symbol, quantity):
    ib = IB()
    try:
        print("Connecting to IB...")
        await ib.connectAsync('127.0.0.1', 4001, clientId=5)
        print("Connected to IB.")

        # יצירת חוזה מניה
        stock = Stock(symbol, 'SMART', 'USD')
        print(f"Qualifying contract for {symbol}...")
        contract_details = await ib.qualifyContractsAsync(stock)
        
        if not contract_details:
            return {"error": f"Invalid symbol: {symbol}"}

        ib.reqMarketDataType(3)  # 3 מציין Delayed Data

        # בדיקת שעות מסחר
        ticker = ib.reqMktData(stock, snapshot=True)
        await asyncio.sleep(2)

        if ticker.bid is None and ticker.ask is None:
            return {"error": f"Market for {symbol} is closed. Cannot place order outside trading hours."}

        # יצירת פקודת קנייה
        print(f"Placing BUY order for {quantity} shares of {symbol}...")
        order = MarketOrder('BUY', quantity)
        trade = ib.placeOrder(stock, order)

        # החזרת סטטוס מיידי של ההזמנה
        return {
            "status": "Order Placed",
            "symbol": symbol,
            "quantity": quantity,
            "action": "BUY",
            "order_status": trade.orderStatus.status
        }

    except Exception as e:
        print(f"Error: {e}")
        return {"error": str(e)}
    finally:
        ib.disconnect()
        print("Disconnected from IB.")


async def sell_stock(symbol, quantity):
    ib = IB()
    try:
        print("Connecting to IB...")
        await ib.connectAsync('127.0.0.1', 4001, clientId=6)
        print("Connected to IB.")

        # יצירת חוזה מניה
        stock = Stock(symbol, 'SMART', 'USD')
        print(f"Qualifying contract for {symbol}...")
        contract_details = await ib.qualifyContractsAsync(stock)

        if not contract_details:
            print(f"Invalid symbol: {symbol}")
            return {"error": f"Invalid symbol: {symbol}"}

        print(f"Placing SELL order for {quantity} shares of {symbol}...")
        # יצירת פקודת מכירה
        order = MarketOrder('SELL', quantity)
        trade = ib.placeOrder(stock, order)

        # ממתינים שההזמנה תושלם עם הגבלת זמן
        try:
            while not trade.isDone():
                print("Waiting for trade to complete...")
                await asyncio.sleep(1)
        except TimeoutError:
            return {"error": "Order placement timed out"}

        print("Trade completed successfully.")
        return {
            "status": "Order Placed Successfully",
            "symbol": symbol,
            "quantity": quantity,
            "action": "SELL",
            "order_status": trade.orderStatus.status
        }
    except Exception as e:
        print(f"Error: {e}")
        return {"error": str(e)}
    finally:
        ib.disconnect()
        print("Disconnected from IB.")

async def fetch_stock_data(symbol):
    ib = IB()
    try:
        await ib.connectAsync('127.0.0.1', 4001, clientId=2)
        ib.reqMarketDataType(3)  # Delayed data

        stock = Stock(symbol, 'SMART', 'USD')
        ticker = ib.reqMktData(stock, snapshot=True)
        await asyncio.sleep(2)

        status = "Market Open" if ticker.bid is not None and ticker.ask is not None else "Market Closed"

        if ticker.close is not None:
            return {
                "symbol": symbol,
                "price": ticker.close,
                "bid": ticker.bid,
                "ask": ticker.ask,
                "status": status
            }
        else:
            return {"error": f"Unable to fetch market data for {symbol}"}
    except Exception as e:
        return {"error": str(e)}
    finally:
        ib.disconnect()

async def fetch_portfolio_data():
    ib = IB()
    try:
        await ib.connectAsync('127.0.0.1', 4001, clientId=3)
        portfolio = []

        for position in ib.portfolio():
            symbol = position.contract.symbol
            quantity = position.position
            purchase_price = position.averageCost

            # שליפת נתוני המניה
            stock_data = await fetch_stock_data(symbol)

            if "error" in stock_data:
                current_price = 0.0
                ask_price = 0.0
                status = "Unknown"
            else:
                current_price = stock_data["price"]
                ask_price = stock_data["ask"] or current_price  # אם אין ask, נשתמש במחיר הנוכחי
                status = stock_data["status"]

            # חישוב הרווח הכללי
            current_value = current_price * quantity
            total_profit_loss = current_value - (quantity * purchase_price)

            # חישוב הרווח היומי אם השוק פתוח
            if status == "Market Open":
                ask_value = ask_price * quantity
                daily_profit_loss = ask_value - current_value
            else:
                daily_profit_loss = 0.0

            portfolio.append({
                "symbol": symbol,
                "quantity": quantity,
                "purchase_price": round(purchase_price, 2),
                "current_price": round(current_price, 2),
                "current_value": round(current_value, 2),
                "total_profit_loss": round(total_profit_loss, 2),
                "daily_profit_loss": round(daily_profit_loss, 2),
                "status": status
            })

        return portfolio
    except Exception as e:
        return {"error": str(e)}
    finally:
        ib.disconnect()


async def fetch_portfolio_summary():
    ib = IB()
    try:
        await ib.connectAsync('127.0.0.1', 4001, clientId=4)
        portfolio = await fetch_portfolio_data()

        # שליפת יתרת המזומן מהסיכום הכללי
        account_summary = await ib.accountSummaryAsync()
        cash_balance = 0.0

        # בדיקה אם יש שדה של מזומן בסיכום
        for item in account_summary:
            if item.tag == 'AvailableFunds':
                cash_balance = float(item.value)

        # חישוב סיכומים
        total_value = cash_balance
        total_profit_loss = sum(item['total_profit_loss'] for item in 


if 'total_profit_loss' in item)
        total_daily_profit_loss = sum(item['daily_profit_loss'] for item in portfolio if 'daily_profit_loss' in item)

        # החזרת סיכום כולל
        return {
            "user_id": f"user_{ib.client.clientId}",
            "total_value": round(total_value, 2),
            "cash_balance": round(cash_balance, 2),
            "total_profit_loss": round(total_profit_loss, 2),
            "total_daily_profit_loss": round(total_daily_profit_loss, 2),
            "status": "Portfolio Summary Generated Successfully"
        }

    except Exception as e:
        return {"error": str(e)}
    finally:
        ib.disconnect()

async def fetch_detailed_financial_summary():
    ib = IB()
    try:
        await ib.connectAsync('127.0.0.1', 4001, clientId=7)
        
        # שליפת יתרת המזומן
        account_summary = await ib.accountSummaryAsync()
        cash_balance = 0.0
        net_liquidation = 0.0
        available_funds = 0.0

        for item in account_summary:
            if item.tag == 'NetLiquidation':
                net_liquidation = float(item.value)
            elif item.tag == 'AvailableFunds':
                available_funds = float(item.value)
            elif item.tag == 'CashBalance':
                cash_balance = float(item.value)

        # שליפת נתוני פורטפוליו
        portfolio = await fetch_portfolio_data()
        total_invested = sum(item['current_value'] for item in portfolio)

        return {
            "cash_balance": round(cash_balance, 2),
            "net_liquidation": round(net_liquidation, 2),
            "available_funds": round(available_funds, 2),
            "total_invested": round(total_invested, 2),
            "status": "Detailed Financial Summary Generated Successfully"
        }

    except Exception as e:
        return {"error": str(e)}
    finally:
        ib.disconnect()
