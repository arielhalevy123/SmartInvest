from quart import Quart, request, jsonify
from ib_utils import fetch_stock_data, fetch_portfolio_data, fetch_portfolio_summary, buy_stock, sell_stock
from ib_utils import fetch_detailed_financial_summary

app = Quart(__name__)
API_KEY = "12345-abcde-67890-fghij-12345"

@app.route('/get_stock_info', methods=['GET'])
async def get_stock_info():
    key = request.headers.get('x-api-key')
    if key != API_KEY:
        return jsonify({"error": "Unauthorized"}), 401

    info_type = request.args.get('type', '').lower()

    if info_type == 'price':
        symbol = request.args.get('symbol', '').upper()
        if not symbol:
            return jsonify({"error": "Symbol is required"}), 400
        data = await fetch_stock_data(symbol)
        return jsonify(data)

    elif info_type == 'portfolio':
        data = await fetch_portfolio_data()
        return jsonify(data)

    elif info_type == 'summary':
        data = await fetch_portfolio_summary()
        return jsonify(data)

    else:
        return jsonify({"error": "Invalid type parameter"}), 400


@app.route('/get_financial_summary', methods=['GET'])
async def get_financial_summary():
    key = request.headers.get('x-api-key')
    if key != API_KEY:
        return jsonify({"error": "Unauthorized"}), 401

    data = await fetch_detailed_financial_summary()
    return jsonify(data)

@app.route('/buy_stock', methods=['POST'])
async def handle_buy_stock():
    key = request.headers.get('x-api-key')
    if key != API_KEY:
        return jsonify({"error": "Unauthorized"}), 401

    data = await request.get_json()
    symbol = data.get('symbol', '').upper()
    quantity = data.get('quantity', 0)

    if not symbol or quantity <= 0:
        return jsonify({"error": "Invalid symbol or quantity"}), 400

    result = await buy_stock(symbol, quantity)
    return jsonify(result)

@app.route('/sell_stock', methods=['POST'])
async def handle_sell_stock():
    key = request.headers.get('x-api-key')
    if key != API_KEY:
        return jsonify({"error": "Unauthorized"}), 401

    data = await request.get_json()
    symbol = data.get('symbol', '').upper()
    quantity = data.get('quantity', 0)

    if not symbol or quantity <= 0:
        return jsonify({"error": "Invalid symbol or quantity"}), 400

    result = await sell_stock(symbol, quantity)
    return jsonify(result)
if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
