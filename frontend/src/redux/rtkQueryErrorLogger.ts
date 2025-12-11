// import { isRejectedWithValue } from '@reduxjs/toolkit';
// import type { Middleware } from '@reduxjs/toolkit';
// import { toast } from 'react-toastify';

// /**
//  * Middleware для логирования и отображения ошибок RTK Query.
//  * Оно перехватывает rejected-экшены и показывает toast-уведомление с ошибкой.

//  */
// export const rtkQueryErrorLogger: Middleware = () => (next) => (action) => {
//   // isRejectedWithValue - это функция-предикат, которая проверяет,
//   // является ли экшен rejected-экшеном от thunk'а или RTK Query,
//   // который был отклонен с помощью `rejectWithValue`.
//   if (isRejectedWithValue(action)) {
//     // Большинство ошибок от API будут содержать объект `data` с полем `message`.
//     // Мы пытаемся извлечь это сообщение.
//     const errorMessage =
//       (action.payload as { data: { message: string } })?.data?.message || 'Произошла неизвестная ошибка';

//     // Показываем toast-уведомление с ошибкой.
//     // Вы можете настроить его внешний вид и поведение.
//     toast.error(errorMessage, {
//       position: 'top-right',
//       autoClose: 5000,
//       hideProgressBar: false,
//       closeOnClick: true,
//       pauseOnHover: true,
//       draggable: true,
//     });
//   }

//   return next(action);
// };
