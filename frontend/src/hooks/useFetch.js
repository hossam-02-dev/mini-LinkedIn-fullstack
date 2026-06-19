import { useState, useEffect, useCallback } from 'react';
import toast from 'react-hot-toast';

const useFetch = (fetchFunction, dependencies = [], options = {}) => {
  const { immediate = true, showError = true } = options;
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const execute = useCallback(async (...args) => {
    setLoading(true);
    setError(null);
    try {
      const result = await fetchFunction(...args);
      setData(result.data);
      return result.data;
    } catch (err) {
      setError(err);
      if (showError) {
        toast.error(err.response?.data?.message || 'Une erreur est survenue');
      }
      throw err;
    } finally {
      setLoading(false);
    }
  }, [fetchFunction, showError]);

  useEffect(() => {
    if (immediate) {
      execute();
    }
  }, [execute, ...dependencies]);

  return { data, loading, error, execute, setData };
};

export default useFetch;