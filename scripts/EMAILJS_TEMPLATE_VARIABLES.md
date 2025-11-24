# 📧 Variables Disponibles en el Template de EmailJS

## Variables que se envían al template de EmailJS

Cuando se envía un correo de confirmación de compra, se incluyen las siguientes variables:

### Información del Usuario
- `to_name` - Nombre completo del usuario
- `to_email` - Email del usuario
- `user_address` - **Dirección del usuario** (nuevo) ⭐

### Información del Pedido
- `tracking_number` - Número de seguimiento del pedido
- `payment_id` - ID del pago de MercadoPago
- `purchase_date` - Fecha de la compra (formato: dd/MM/yyyy HH:mm)

### Información Financiera
- `subtotal` - Subtotal sin IVA (formato: número entero)
- `iva` - IVA del 19% (formato: número entero)
- `shipping` - Costo de envío (solo si es mayor a 0)
- `total_price` - Precio total con IVA incluido (formato: número entero)

### Items de la Compra
- `items` - Array de objetos con la siguiente estructura:
  ```json
  [
    {
      "name": "Nombre del producto",
      "quantity": "1",
      "unit_price": "1000",
      "total_price": "1000",
      "image_url": "https://..." // Opcional, si está disponible
    }
  ]
  ```

## 📝 Cómo Usar en el Template de EmailJS

### Ejemplo básico:
```
Hola {{to_name}},

Tu pedido #{{tracking_number}} ha sido confirmado.

Dirección de envío:
{{user_address}}

Total a pagar: ${{total_price}}
```

### Ejemplo con items:
```
Items comprados:
{{#each items}}
- {{name}} x{{quantity}} - ${{total_price}}
{{/each}}
```

## ⚠️ Nota Importante

La variable `user_address` puede estar vacía si:
- El usuario no tiene dirección registrada
- El usuario es invitado y no proporcionó dirección

En ese caso, la variable simplemente no se incluirá en el template_params, o estará vacía.

## 🔍 Verificación

Para verificar que la dirección se está enviando:
1. Revisa los logs de la app (Logcat) buscando "EmailJSService"
2. Deberías ver: `Dirección del usuario: [dirección]` o `No especificada`
3. En el template de EmailJS, usa `{{user_address}}` para mostrar la dirección

