from flask import Flask, request, jsonify
from flask_cors import CORS
from datetime import datetime, timedelta
import json

app = Flask(__name__)
CORS(app)

print("=" * 50)
print("🐍 Python Service - FinanceApp")
print("=" * 50)

@app.route('/health', methods=['GET'])
def health():
    """Verificar se o serviço está rodando"""
    return jsonify({
        'status': 'ok', 
        'service': 'finance-analyzer',
        'message': 'Python service está funcionando!'
    })

@app.route('/dashboard-data', methods=['POST'])
def dashboard_data():
    """Gera todos os dados para o dashboard (gráficos e insights)"""
    try:
        data = request.json
        transactions = data.get('transactions', [])
        start_date = data.get('startDate', '')
        end_date = data.get('endDate', '')
        
        print(f"📊 Analisando {len(transactions)} transações")
        
        # Separar receitas e despesas
        incomes = [t for t in transactions if t.get('type') == 'INCOME']
        expenses = [t for t in transactions if t.get('type') == 'EXPENSE']
        
        # Calcular totais
        total_incomes = sum(t.get('amount', 0) for t in incomes)
        total_expenses = sum(t.get('amount', 0) for t in expenses)
        balance = total_incomes - total_expenses
        
        # ========== GRÁFICO 1: Despesas por Categoria ==========
        expenses_by_category = {}
        for expense in expenses:
            category = expense.get('category', 'Outros')
            amount = expense.get('amount', 0)
            expenses_by_category[category] = expenses_by_category.get(category, 0) + amount
        
        # Ordenar por valor (maior primeiro)
        sorted_categories = sorted(expenses_by_category.items(), key=lambda x: x[1], reverse=True)
        
        chart_categories = {
            'labels': [item[0] for item in sorted_categories],
            'values': [item[1] for item in sorted_categories],
            'colors': ['#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF', '#FF9F40', '#66BB6A', '#AB47BC']
        }
        
        # ========== GRÁFICO 2: Evolução Mensal ==========
        monthly_data = {}
        for t in transactions:
            date = t.get('transactionDate', '')
            if date:
                # Extrair ano-mês (ex: 2024-01)
                year_month = date[:7]
                amount = t.get('amount', 0)
                type_trans = t.get('type', '')
                
                if year_month not in monthly_data:
                    monthly_data[year_month] = {'incomes': 0, 'expenses': 0}
                
                if type_trans == 'INCOME':
                    monthly_data[year_month]['incomes'] += amount
                else:
                    monthly_data[year_month]['expenses'] += amount
        
        # Ordenar por data
        sorted_months = sorted(monthly_data.keys())
        monthly_chart = {
            'labels': sorted_months,
            'incomes': [monthly_data[m]['incomes'] for m in sorted_months],
            'expenses': [monthly_data[m]['expenses'] for m in sorted_months],
            'balances': [monthly_data[m]['incomes'] - monthly_data[m]['expenses'] for m in sorted_months]
        }
        
        # ========== INSIGHTS ==========
        insights = []
        
        # Insight 1: Comparação receita vs despesa
        if total_expenses > total_incomes:
            insights.append({
                'type': 'danger',
                'icon': '⚠️',
                'title': 'Alerta Financeiro!',
                'message': f'Você gastou R$ {total_expenses - total_incomes:.2f} a mais do que ganhou',
                'suggestion': 'Revise seus gastos urgentemente para não ficar no vermelho'
            })
        elif total_incomes > 0:
            savings_rate = ((total_incomes - total_expenses) / total_incomes) * 100
            if savings_rate < 20:
                insights.append({
                    'type': 'warning',
                    'icon': '📊',
                    'title': 'Taxa de Economia Baixa',
                    'message': f'Você economiza apenas {savings_rate:.1f}% da sua renda',
                    'suggestion': 'Tente economizar pelo menos 20% da sua renda mensal'
                })
            else:
                insights.append({
                    'type': 'success',
                    'icon': '🎉',
                    'title': 'Excelente!',
                    'message': f'Você economiza {savings_rate:.1f}% da sua renda',
                    'suggestion': 'Continue assim! Invista o excedente para seu futuro'
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
                'message': f'Sua maior despesa é com {top_category}',
                'suggestion': f'Total: R$ {top_amount:.2f} ({percentage:.1f}% dos gastos). Tente reduzir!'
            })
        
        # Insight 3: Média de gastos por dia
        if expenses and len(transactions) > 0:
            # Calcular número de dias no período
            try:
                start = datetime.strptime(start_date, '%Y-%m-%d') if start_date else None
                end = datetime.strptime(end_date, '%Y-%m-%d') if end_date else None
                if start and end:
                    days = (end - start).days + 1
                    daily_avg = total_expenses / days if days > 0 else 0
                    
                    if daily_avg > 100:
                        insights.append({
                            'type': 'warning',
                            'icon': '📅',
                            'title': 'Gasto Diário Alto',
                            'message': f'Você gasta em média R$ {daily_avg:.2f} por dia',
                            'suggestion': f'Isso representa R$ {daily_avg * 30:.2f} por mês. Tente reduzir para R$ 50/dia'
                        })
            except:
                pass
        
        # Insight 4: Pequenos gastos frequentes
        small_expenses = [e for e in expenses if e.get('amount', 0) < 50]
        if len(small_expenses) > 5:
            total_small = sum(e.get('amount', 0) for e in small_expenses)
            insights.append({
                'type': 'info',
                'icon': '💡',
                'title': 'Pequenos Gastos',
                'message': f'Você fez {len(small_expenses)} pequenos gastos (< R$ 50)',
                'suggestion': f'Total acumulado: R$ {total_small:.2f}. Pequenas economias ajudam muito!'
            })
        
        # Insight 5: Dica baseada no número de transações
        if len(transactions) < 5:
            insights.append({
                'type': 'info',
                'icon': '📝',
                'title': 'Comece a Registrar',
                'message': 'Você tem poucas transações registradas',
                'suggestion': 'Registre todas as suas despesas e receitas para ter insights mais precisos'
            })
        
        # ========== RETORNAR TUDO ==========
        return jsonify({
            'success': True,
            'summary': {
                'totalIncomes': total_incomes,
                'totalExpenses': total_expenses,
                'balance': balance
            },
            'charts': {
                'categoryChart': chart_categories,
                'monthlyChart': monthly_chart
            },
            'insights': insights,
            'stats': {
                'totalTransactions': len(transactions),
                'totalIncomesCount': len(incomes),
                'totalExpensesCount': len(expenses),
                'averageExpense': total_expenses / len(expenses) if expenses else 0,
                'averageIncome': total_incomes / len(incomes) if incomes else 0
            }
        })
        
    except Exception as e:
        print(f"❌ Erro: {str(e)}")
        return jsonify({
            'success': False,
            'error': str(e)
        }), 500

@app.route('/analyze', methods=['POST'])
def analyze():
    """Endpoint antigo para compatibilidade"""
    data = request.json
    transactions = data.get('transactions', [])
    current_balance = data.get('balance', 0)
    
    insights = []
    
    if transactions:
        total_expenses = sum(t['amount'] for t in transactions if t['type'] == 'EXPENSE')
        total_incomes = sum(t['amount'] for t in transactions if t['type'] == 'INCOME')
        
        if total_expenses > total_incomes:
            insights.append({
                'type': 'warning',
                'title': '⚠️ Alerta',
                'message': 'Seus gastos estão maiores que suas receitas!',
                'suggestion': 'Revise seus gastos urgentemente.'
            })
        
        categories = {}
        for t in transactions:
            if t['type'] == 'EXPENSE':
                cat = t.get('category', 'Outros')
                categories[cat] = categories.get(cat, 0) + t['amount']
        
        if categories:
            top_category = max(categories, key=categories.get)
            insights.append({
                'type': 'info',
                'title': '📊 Análise',
                'message': f'Você gasta mais com {top_category}',
                'suggestion': f'Total: R$ {categories[top_category]:.2f}. Tente reduzir.'
            })
    
    return jsonify(insights)

@app.route('/test', methods=['GET'])
def test():
    """Endpoint de teste"""
    return jsonify({
        'message': 'Python service está funcionando!',
        'status': 'online',
        'endpoints': ['GET /health', 'GET /test', 'POST /dashboard-data', 'POST /analyze']
    })

if __name__ == '__main__':
    print("=" * 50)
    print("📡 Servidor rodando em: http://localhost:5000")
    print("📊 Endpoints disponíveis:")
    print("   GET  /health        - Verificar saúde")
    print("   GET  /test          - Testar conexão")
    print("   POST /dashboard-data - Dados para dashboard")
    print("   POST /analyze       - Insights")
    print("=" * 50)
    app.run(host='0.0.0.0', port=5000, debug=False, use_reloader=False)