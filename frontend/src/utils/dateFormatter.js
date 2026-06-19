import { format, formatDistanceToNow, formatDistanceToNowStrict } from 'date-fns';
import { fr } from 'date-fns/locale';

export const formatDate = (date, formatStr = 'dd/MM/yyyy') => {
  if (!date) return '';
  return format(new Date(date), formatStr, { locale: fr });
};

export const formatDateTime = (date) => {
  if (!date) return '';
  return format(new Date(date), 'dd/MM/yyyy à HH:mm', { locale: fr });
};

export const timeAgo = (date) => {
  if (!date) return '';
  return formatDistanceToNow(new Date(date), { addSuffix: true, locale: fr });
};

export const timeAgoStrict = (date) => {
  if (!date) return '';
  return formatDistanceToNowStrict(new Date(date), { locale: fr });
};