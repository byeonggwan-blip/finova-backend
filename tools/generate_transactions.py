import json
import random
from datetime import datetime, timedelta
from pathlib import Path

random.seed(42)

project_root = Path(__file__).resolve().parent.parent
json_path = project_root / "src/main/resources/mock-bank/transactions.json"

with json_path.open("r", encoding="utf-8") as f:
    data = json.load(f)

# 기존 가상 은행 거래내역 유지
existing = data["transactions"]
existing_ids = {
    tx["externalTransactionId"]
    for tx in existing
}

categories = {
    "식비": (6000, 25000),
    "교통": (1500, 15000),
    "카페": (3500, 9000),
    "쇼핑": (20000, 120000),
    "생활": (10000, 80000),
    "문화": (8000, 50000),
    "통신": (30000, 70000)
}

start_date = datetime(2026, 1, 1)
end_date = datetime(2026, 10, 23, 23, 59, 59)

new_transactions = []

for i in range(150):
    external_id = f"DEMO-{1005 + i}"

    if external_id in existing_ids:
        continue

    category = random.choice(list(categories))
    minimum, maximum = categories[category]

    amount = random.randint(minimum, maximum)

    random_seconds = random.randint(
        0, int((end_date - start_date).total_seconds())
    )

    transaction_date = start_date + timedelta(seconds=random_seconds)

    new_transactions.append({
        "externalTransactionId": external_id,
        "transactionType": "EXPENSE",
        "category": category,
        "amount": amount,
        "description": f"{category} 이용",
        "transactionDate": transaction_date.isoformat(timespec="seconds")
    })

# 매월 급여 생성 (기존 10월 급여는 유지)
for month in range(1, 11):

    # 10월 급여는 DEMO-1001에 이미 존재하므로 제외
    if month == 10:
        continue

    external_id = f"DEMO-SALARY-2026-{month:02d}"

    if external_id in existing_ids:
        continue

    new_transactions.append({
        "externalTransactionId": external_id,
        "transactionType": "INCOME",
        "category": "급여",
        "amount": 3200000,
        "description": f"{month}월 급여",
        "transactionDate": f"2026-{month:02d}-20T09:00:00"
    })

data["transactions"].extend(new_transactions)

with json_path.open("w", encoding="utf-8") as f:
    json.dump(data, f, ensure_ascii=False, indent=2)

print(f"새로 생성한 거래: {len(new_transactions)}건")
print(f"전체 거래: {len(data['transactions'])}건")
print(f"저장 위치: {json_path}")