package furniture.shop.configure.valid;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumValidator implements ConstraintValidator<EnumValue, Enum<?>> {

    private EnumValue enumValue;

    @Override
    public void initialize(EnumValue constraintAnnotation) {
        this.enumValue = constraintAnnotation;
    }

    @Override
    public boolean isValid(Enum<?> value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        try {
            Enum<?>[] enumValues = this.enumValue.enumClass().getEnumConstants();

            if (enumValues != null) {
                for (Enum<?> enumValue : enumValues) {
                    if (value.equals(enumValue)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }


        return false;
    }
}
