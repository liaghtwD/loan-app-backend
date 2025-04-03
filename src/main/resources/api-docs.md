# 贷款应用API文档

## 用户模块

### 1. 用户登录

**API**: `POST /api/user/login`

**Request**:
```json
{
  "phone": "18612345678",
  "password": "password123"
}
```

**Response**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "userId": 1
  }
}
```

### 2. 用户注册

**API**: `POST /api/user/register`

**Request**:
```json
{
  "phone": "18612345678",
  "password": "password123"
}
```

**Response**:
```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "userId": 1
  }
}
```

### 3. 获取用户资料

**API**: `GET /api/user/profile`

**Headers**:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**Response**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "phone": "18612345678",
    "email": "user@example.com",
    "status": 1,
    "createTime": "2025-03-20T10:00:00",
    "lastLoginTime": "2025-03-27T14:30:00",
    "creditScore": 80,
    "creditLimit": 50000.00,
    "usedCredit": 10000.00,
    "availableCredit": 40000.00
  }
}
```

## 信用与额度模块

### 1. 获取用户信用信息

**API**: `GET /api/user/credit`

**Headers**:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**Response**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "creditScore": 80,
    "creditLimit": 50000.00,
    "usedCredit": 10000.00,
    "availableCredit": 40000.00
  }
}
```

## 贷款模块

### 1. 申请贷款

**API**: `POST /api/loan/apply`

**Headers**:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**Request**:
```json
{
  "loanAmount": 10000.00,
  "loanPeriod": 6,
  "repaymentMethod": 1,
  "loanPurpose": "购买电子设备"
}
```

**Response**:
```json
{
  "code": 200,
  "message": "贷款申请成功",
  "data": 123
}
```

### 2. 获取贷款详情

**API**: `GET /api/loan/{loanId}`

**Headers**:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**Response**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 123,
    "userId": 1,
    "loanAmount": 10000.00,
    "loanPeriod": 6,
    "repaymentMethod": 1,
    "interestRate": 0.05,
    "loanPurpose": "购买电子设备",
    "actualLoanAmount": 10000.00,
    "status": 1,
    "applyTime": "2025-03-27T10:00:00",
    "approveTime": "2025-03-27T11:00:00",
    "updateTime": "2025-03-27T11:00:00",
    "remainingInstallments": 6,
    "paidInstallments": 0
  }
}
```

### 3. 更新贷款申请

**API**: `PUT /api/loan/update/{loanId}`

**Headers**:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**Request**:
```json
{
  "loanAmount": 8000.00,
  "loanPeriod": 3,
  "repaymentMethod": 0,
  "loanPurpose": "旅游",
  "status": 5
}
```

**Response**:
```json
{
  "code": 200,
  "message": "更新成功",
  "data": true
}
```

### 4. 贷款模拟计算

**API**: `POST /api/loan/simulation`

**Headers**:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**Request**:
```json
{
  "loanAmount": 10000.00,
  "loanPeriod": 6,
  "installment": true
}
```

**Response**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "loanAmount": 10000.00,
    "loanPeriod": 6,
    "installment": true,
    "interestRate": 0.05,
    "totalRepayment": 10250.00,
    "principalRepaymentPlans": [
      {
        "installmentNo": 1,
        "amountDue": 1708.33,
        "principal": 1666.67,
        "interest": 41.67
      },
      {
        "installmentNo": 2,
        "amountDue": 1701.39,
        "principal": 1666.67,
        "interest": 34.72
      },
      {
        "installmentNo": 3,
        "amountDue": 1694.44,
        "principal": 1666.67,
        "interest": 27.78
      },
      {
        "installmentNo": 4,
        "amountDue": 1687.50,
        "principal": 1666.67,
        "interest": 20.83
      },
      {
        "installmentNo": 5,
        "amountDue": 1680.56,
        "principal": 1666.67,
        "interest": 13.89
      },
      {
        "installmentNo": 6,
        "amountDue": 1673.61,
        "principal": 1666.65,
        "interest": 6.94
      }
    ],
    "annuityRepaymentPlans": [
      {
        "installmentNo": 1,
        "amountDue": 1707.37,
        "principal": 1665.70,
        "interest": 41.67
      },
      {
        "installmentNo": 2,
        "amountDue": 1707.37,
        "principal": 1672.64,
        "interest": 34.73
      },
      {
        "installmentNo": 3,
        "amountDue": 1707.37,
        "principal": 1679.60,
        "interest": 27.77
      },
      {
        "installmentNo": 4,
        "amountDue": 1707.37,
        "principal": 1686.60,
        "interest": 20.77
      },
      {
        "installmentNo": 5,
        "amountDue": 1707.37,
        "principal": 1693.63,
        "interest": 13.74
      },
      {
        "installmentNo": 6,
        "amountDue": 1707.37,
        "principal": 1700.83,
        "interest": 6.54
      }
    ]
  }
}
```

### 5. 获取当前贷款

**API**: `GET /api/loan/now`

**Headers**:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**Response**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 123,
    "userId": 1,
    "loanAmount": 10000.00,
    "loanPeriod": 6,
    "repaymentMethod": 1,
    "interestRate": 0.05,
    "loanPurpose": "购买电子设备",
    "actualLoanAmount": 10000.00,
    "status": 1,
    "applyTime": "2025-03-27T10:00:00",
    "approveTime": "2025-03-27T11:00:00",
    "updateTime": "2025-03-27T11:00:00",
    "remainingInstallments": 5,
    "paidInstallments": 1
  }
}
```

### 6. 获取贷款历史

**API**: `GET /api/user/loan-history`

**Headers**:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**Response**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 123,
      "userId": 1,
      "loanAmount": 10000.00,
      "loanPeriod": 6,
      "repaymentMethod": 1,
      "interestRate": 0.05,
      "loanPurpose": "购买电子设备",
      "actualLoanAmount": 10000.00,
      "status": 1,
      "applyTime": "2025-03-27T10:00:00",
      "approveTime": "2025-03-27T11:00:00",
      "updateTime": "2025-03-27T11:00:00"
    },
    {
      "id": 100,
      "userId": 1,
      "loanAmount": 5000.00,
      "loanPeriod": 3,
      "repaymentMethod": 0,
      "interestRate": 0.05,
      "loanPurpose": "旅游",
      "actualLoanAmount": 5000.00,
      "status": 2,
      "applyTime": "2025-02-15T14:00:00",
      "approveTime": "2025-02-15T15:00:00",
      "updateTime": "2025-03-15T16:00:00"
    }
  ]
}
```

## 还款模块

### 1. 还款

**API**: `POST /api/repayment/pay`

**Headers**:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**Request**:
```json
{
  "loanId": 123,
  "amount": 1707.37,
  "prepayment": false,
  "installmentNo": 1
}
```

**Response**:
```json
{
  "code": 200,
  "message": "还款成功",
  "data": true
}
```

### 2. 获取还款历史

**API**: `GET /api/repayment/history`

**Headers**:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**Response**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "loanId": 123,
      "userId": 1,
      "installmentNo": 1,
      "repaymentAmount": 1707.37,
      "repaymentTime": "2025-04-27T10:00:00",
      "status": 0,
      "actualRepaymentTime": "2025-04-25T16:30:00",
      "updateTime": "2025-04-25T16:30:00"
    },
    {
      "id": 2,
      "loanId": 100,
      "userId": 1,
      "installmentNo": 1,
      "repaymentAmount": 5062.50,
      "repaymentTime": "2025-03-15T15:00:00",
      "status": 1,
      "actualRepaymentTime": "2025-03-10T14:20:00",
      "updateTime": "2025-03-10T14:20:00"
    }
  ]
}
``` 