'use client';

import { useState } from 'react';
import { useRegisterMutation, useLoginMutation, useLogoutMutation } from '@/redux/auth/auth';
import { useLazyGetMeQuery } from '@/redux/api';
import { useSelector, useDispatch } from 'react-redux';
import { RootState } from '@/redux/store';
import { setTokens } from '@/redux/auth/authSlice';
import Cookies from 'js-cookie';

export function AuthFlowTest() {
  // --- Hooks ---
  const [register] = useRegisterMutation();
  const [login] = useLoginMutation();
  const [logout] = useLogoutMutation();
  const [triggerGetMe] = useLazyGetMeQuery();
  const dispatch = useDispatch();

  // --- State ---
  const { accessToken, isAuth, userId } = useSelector((state: RootState) => state.auth);
  const [email, setEmail] = useState(`testuser${Math.floor(Math.random() * 1000)}@test.com`);
  const [password, setPassword] = useState('password123');
  const [logs, setLogs] = useState<string[]>([]);

  const addLog = (log: string) => setLogs(prev => [`[${new Date().toLocaleTimeString()}] ${log}`, ...prev]);

  // --- Handlers ---
  const handleRegister = async () => {
    addLog('Шаг 1: Попытка регистрации...');
    try {
      await register({ login: email, password, agreeToTerms: true }).unwrap();
      addLog('✅ Успех: Регистрация прошла успешно.');
    } catch (err: any) {
      addLog(`❌ Ошибка регистрации: ${JSON.stringify(err.data)}`);
    }
  };

  const handleLogin = async () => {
    addLog('Шаг 2: Попытка входа...');
    try {
      await login({ login: email, password }).unwrap();
      addLog(`✅ Успех: Вход выполнен.`);
    } catch (err: any) {
      addLog(`❌ Ошибка входа: ${JSON.stringify(err.data)}`);
    }
  };
  
  const handleGetMe = async () => {
    addLog('Шаг 3: Запрос к защищенному эндпоинту /me...');
    try {
        const response = await triggerGetMe().unwrap();
        addLog(`✅ Успех /me: Получены данные: ${JSON.stringify(response)}`);
    } catch (err: any) {
        addLog(`❌ Ошибка /me: ${JSON.stringify(err)}`);
    }
  };

  const handleCorruptAndGetMe = async () => {
    addLog('Шаг 4: "Портим" accessToken и запрашиваем /me...');
    if (accessToken && userId) {
        dispatch(setTokens({ accessToken: 'invalid-token' }));
        addLog('Токен испорчен в Redux. Выполняю запрос...');
        await handleGetMe();
    } else {
        addLog('Невозможно испортить токен: сначала войдите в систему.');
    }
  };

  const handleLogout = async () => {
    addLog('Шаг 5: Выход из системы...');
    try {
      await logout().unwrap();
      addLog('✅ Успех: Выход выполнен.');
    } catch (err: any) {
      addLog(`❌ Ошибка выхода: ${JSON.stringify(err.data)}`);
    }
  };
  
  return (
    <div style={{ padding: '1rem', fontFamily: 'monospace', display: 'flex', gap: '2rem' }}>
      <div>
        <h2>Пошаговый тест аутентификации</h2>
        <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem', marginBottom: '1rem' }}>
            <input value={email} onChange={(e) => setEmail(e.target.value)} />
            <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
        </div>
        <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
            <button onClick={handleRegister}>1. Регистрация</button>
            <button onClick={handleLogin}>2. Логин</button>
            <button onClick={handleGetMe} disabled={!isAuth}>3. Запросить /me</button>
            <button onClick={handleCorruptAndGetMe} disabled={!isAuth}>4. Тест обновления токена</button>
            <button onClick={handleLogout} disabled={!isAuth}>5. Выход</button>
        </div>
        <hr />
        <h4>Состояние</h4>
        <p>isAuth: <strong>{isAuth.toString()}</strong></p>
        <p>UserID: <strong>{userId}</strong></p>
        <p>AccessToken: <small>{accessToken || 'null'}</small></p>
        <p>RefreshToken (в cookie): <small>{Cookies.get('refresh_token') || 'null'}</small></p>
      </div>
      <div>
        <h3>Логи</h3>
        <div style={{ background: '#f0f0f0', height: '400px', overflowY: 'scroll', padding: '0.5rem', border: '1px solid #ccc' }}>
            {logs.map((log, i) => <div key={i} style={{ borderBottom: '1px solid #ddd', padding: '4px 0' }}>{log}</div>)}
        </div>
      </div>
    </div>
  );
}