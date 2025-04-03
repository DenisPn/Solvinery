import React from 'react';

const Checkbox = ({
  key,
  label,
  checked = false,
  disabled = false,
  onChange,
  name
}) => {
  return (
    <div class="inputGroup">
      <input
        id={key}
        type="checkbox"
        name={name}
        checked={checked}
        disabled={disabled}
        onChange={(e) => onChange?.(e.target.checked)}
        
      />
      <label for={key} >{label}</label>

    </div>
  );
};

export default Checkbox;