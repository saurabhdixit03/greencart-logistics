import React, { createContext, useContext, useState, useEffect } from 'react';
import axios from 'axios';
import toast from 'react-hot-toast';

const AuthContext = createContext();

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

// Set up axios defaults
const API_BASE_URL = process.env.REACT_APP_API_URL || 'https://greencart-logistics-1-3fn4.onrender.com/api';
axios.defaults.baseURL = API_BASE_URL;

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [token, setToken] = useState(localStorage.getItem('token'));

  // Set up axios interceptor for auth token
  useEffect(() => {
    if (token) {
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    } else {
      delete axios.defaults.headers.common['Authorization'];
    }
  }, [token]);

  // Check if user is logged in on app start
  useEffect(() => {
    const checkAuth = async () => {
      const savedToken = localStorage.getItem('token');
      const savedUser = localStorage.getItem('user');

      if (savedToken && savedUser) {
        setToken(savedToken);
        setUser(JSON.parse(savedUser));

        // Validate token with backend
        try {
          await axios.get('/auth/validate');
        } catch (error) {
          // Token is invalid, clear it
          logout();
        }
      }

      setLoading(false);
    };

    checkAuth();
  }, []);

  const login = async (username, password) => {
    try {
      setLoading(true);
      console.log('Attempting login to:', API_BASE_URL + '/auth/signin');

      const response = await axios.post('/auth/signin', {
        username,
        password,
      });

      console.log('Login response:', response.data);

      // Backend sends jwt, not token
      const { jwt, ...userData } = response.data;

      if (!jwt) {
        throw new Error('No token received from server');
      }

      setToken(jwt);
      setUser(userData);

      // Save to localStorage
      localStorage.setItem('token', jwt);
      localStorage.setItem('user', JSON.stringify(userData));

      toast.success('Login successful!');
      return { success: true };

    } catch (error) {
      console.error('Login error:', error);
      console.error('Error response:', error.response?.data);

      let message = 'Login failed';
      if (error.response?.data?.message) {
        message = error.response.data.message;
      } else if (error.response?.status === 401) {
        message = 'Invalid username or password';
      } else if (error.response?.status === 0) {
        message = 'Cannot connect to server';
      } else if (error.message) {
        message = error.message;
      }

      toast.error(message);
      return { success: false, error: message };
    } finally {
      setLoading(false);
    }
  };

  const logout = () => {
    setUser(null);
    setToken(null);
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    delete axios.defaults.headers.common['Authorization'];
    toast.success('Logged out successfully');
  };

  const value = {
    user,
    token,
    login,
    logout,
    loading,
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};
