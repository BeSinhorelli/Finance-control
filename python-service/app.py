import sys
import io

# Forçar encoding UTF-8 no Windows
if sys.platform == 'win32':
    sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')
    sys.stderr = io.TextIOWrapper(sys.stderr.buffer, encoding='utf-8')

from flask import Flask, request, jsonify
from flask_cors import CORS
from datetime import datetime
import json

app = Flask(__name__)
CORS(app)

print("=" * 50)
print("Python Service - FinanceApp")
print("=" * 50)

@app.route('/health', methods=['GET'])
def health():
    return jsonify({'status': 'ok', 'service': 'finance-analyzer'})

@app.route('/analyze', methods=['POST'])
def analyze():
    """Processa transações e retorna dados para dashboard"""
    try:
        data = request.json
        transactions = data.get('transactions', [])

        print(f"Analisando {len(transactions)} transacoes")

        # Separar receitas e despesas
        incomes = [t for t in transactions if t.get('type') == 'INCOME']
        expenses = [t for t in transactions if t.get('type') == 'EXPENSE']

        # Calcular totais
        total_incomes = sum(float(t.get('amount', 0)) for t in incomes)
        total_expenses = sum(float(t.get('amount', 0)) for t in expenses)
        balance = total_incomes - total_expenses

        # GRÁFICO 1: Despesas por categoria
        expenses_by_category = {}
        for expense in expenses:
            category = expense.get('category', 'Outros')
            amount = float(expense.get('amount', 0))
            expenses_by_category[category] = expenses_by_category.get(category, 0) + amount

        # Ordenar por valor (maior primeiro)
        sorted_categories = sorted(expenses_by_category.items(), key=lambda x: x[1], reverse=True)

        # GRÁFICO 2: Evolução mensal
        monthly_data = {}
        for t in transactions:
            date = t.get('transactionDate', '')
            if date:
                year_month = date[:7]  # Ex: 2024-01
                amount = float(t.get('amount', 0))
                type_trans = t.get('type', '')

                if year_month not in monthly_data:
                    monthly_data[year_month] = {'incomes': 0, 'expenses': 0}

                if type_trans == 'INCOME':
                    monthly_data[year_month]['incomes'] += amount
                else:
                    monthly_data[year_month]['expenses'] += amount

        sorted_months = sorted(monthly_data.keys())

        # INSIGHTS
        insights = []

        # Insight 1: Comparação receita vs despesa
        if total_expenses > total_incomes:
            insights.append({
                'type': 'danger',
                'icon': '⚠️',
                'title': 'Alerta Financeiro!',
                'message': f'Voce gastou R$ {total_expenses - total_incomes:.2f} a mais do que ganhou',
                'suggestion': 'Revise seus gastos urgentemente'
            })
        elif total_incomes > 0:
            savings_rate = ((total_incomes - total_expenses) / total_incomes) * 100
            if savings_rate < 20:
                insights.append({
                    'type': 'warning',
                    'icon': '📊',
                    'title': 'Taxa de Economia Baixa',
                    'message': f'Voce economiza apenas {savings_rate:.1f}% da sua renda',
                    'suggestion': 'Tente economizar pelo menos 20% da sua renda mensal'
                })
            else:
                insights.append({
                    'type': 'success',
                    'icon': '🎉',
                    'title': 'Parabens!',
                    'message': f'Voce economiza {savings_rate:.1f}% da sua renda',
                    'suggestion': 'Continue assim! Invista o excedente'
                })

        # Insight 2: Categoria com maior gasto
        if expenses_by_category:
            top_category = max(expenses_by_category, key=expenses_by_category.get)
            top_amount = expenses_by_category[top_category]
            percentage = (top_amount / total_expenses * 100) if total_expenses > 0 else 0
            insights.append({
                'type': 'info',
                'icon': '🎯',
                'title': 'Principal Gasto',
                'message': f'Sua maior despesa e com {top_category}',
                'suggestion': f'Total: R$ {top_amount:.2f} ({percentage:.1f}% dos gastos)'
            })

        # Insight 3: Média diária
        if expenses and transactions:
            dates = [datetime.strptime(t.get('transactionDate'), '%Y-%m-%d') for t in transactions if t.get('transactionDate')]
            if dates:
                date_range = (max(dates) - min(dates)).days + 1
                daily_avg = total_expenses / date_range if date_range > 0 else 0
                if daily_avg > 100:
                    insights.append({
                        'type': 'warning',
                        'icon': '📅',
                        'title': 'Gasto Diario Alto',
                        'message': f'Voce gasta em media R$ {daily_avg:.2f} por dia',
                        'suggestion': f'Isso representa R$ {daily_avg * 30:.2f} por mes'
                    })

        # Insight 4: Pequenos gastos
        small_expenses = [e for e in expenses if e.get('amount', 0) < 50]
        if len(small_expenses) > 5:
            total_small = sum(e.get('amount', 0) for e in small_expenses)
            insights.append({
                'type': 'info',
                'icon': '💡',
                'title': 'Pequenos Gastos',
                'message': f'Voce fez {len(small_expenses)} pequenos gastos (< R$ 50)',
                'suggestion': f'Total acumulado: R$ {total_small:.2f}'
            })

        return jsonify({
            'success': True,
            'summary': {
                'totalIncomes': total_incomes,
                'totalExpenses': total_expenses,
                'balance': balance
            },
            'charts': {
                'categoryChart': {
                    'labels': [item[0] for item in sorted_categories],
                    'values': [item[1] for item in sorted_categories]
                },
                'monthlyChart': {
                    'labels': sorted_months,
                    'incomes': [monthly_data[m]['incomes'] for m in sorted_months],
                    'expenses': [monthly_data[m]['expenses'] for m in sorted_months]
                }
            },
            'insights': insights
        })

    except Exception as e:
        print(f"Erro: {str(e)}")
        return jsonify({'success': False, 'error': str(e)}), 500

@app.route('/test', methods=['GET'])
def test():
    return jsonify({'message': 'Python service esta rodando!', 'status': 'online'})

if __name__ == '__main__':
    print("Servidor rodando em: http://localhost:5000")
    print("Pronto para receber requisicoes")
    print("=" * 50)
    app.run(host='0.0.0.0', port=5000, debug=False, use_reloader=False)