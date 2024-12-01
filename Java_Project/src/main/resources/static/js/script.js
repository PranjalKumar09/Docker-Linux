$(function () {
    // General validation function
    function applyFormValidation(formSelector, rules, messages) {
        let $form = $(formSelector);
        $form.validate({
            rules: rules,
            messages: messages,
            errorElement: 'div',
            errorPlacement: function (error, element) {
                error.addClass('text-danger');
                error.insertAfter(element);
            },
            highlight: function (element) {
                $(element).addClass('is-invalid').removeClass('is-valid');
            },
            unhighlight: function (element) {
                $(element).addClass('is-valid').removeClass('is-invalid');
            }
        });

        // Submit handler to prevent form submission if validation fails
        $form.on('submit', function (e) {
            if (!$(this).valid()) {
                e.preventDefault();
            }
        });
    }

    // Validation for User Registration Form
    applyFormValidation('#userRegister', {
        fullName: {
            required: true,
            minlength: 3
        },
        mobile: {
            required: true,
            digits: true,
            minlength: 10,
            maxlength: 10
        },
        email: {
            required: true,
            email: true
        },
        address: {
            required: true
        },
        city: {
            required: true
        },
        state: {
            required: true
        },
        pincode: {
            required: true,
            digits: true,
            minlength: 6,
            maxlength: 6
        },
        password: {
            required: true,
            minlength: 6
        },
        confirmPassword: {
            required: true,
            equalTo: '[name="password"]'
        },
        img: {
            required: true,
            extension: "jpg|jpeg|png"
        }
    }, {
        fullName: {
            required: "Full Name is required",
            minlength: "Full Name must be at least 3 characters long"
        },
        mobile: {
            required: "Mobile Number is required",
            digits: "Please enter a valid mobile number",
            minlength: "Mobile Number must be exactly 10 digits",
            maxlength: "Mobile Number must be exactly 10 digits"
        },
        email: {
            required: "Email is required",
            email: "Please enter a valid email address"
        },
        address: {
            required: "Address is required"
        },
        city: {
            required: "City is required"
        },
        state: {
            required: "State is required"
        },
        pincode: {
            required: "Pincode is required",
            digits: "Please enter a valid pincode",
            minlength: "Pincode must be exactly 6 digits",
            maxlength: "Pincode must be exactly 6 digits"
        },
        password: {
            required: "Password is required",
            minlength: "Password must be at least 6 characters long"
        },
        confirmPassword: {
            required: "Confirm Password is required",
            equalTo: "Passwords do not match"
        },
        img: {
            required: "Profile Image is required",
            extension: "Please upload an image file (jpg, jpeg, or png)"
        }

    });

    // Validation for Category Form
    applyFormValidation('#categoryForm', {
        title: {
            required: true,
            minlength: 3
        },
        isActive: {
            required: true
        },
        image_name: {
            extension: "jpg|jpeg|png"
        }
    }, {
        title: {
            required: "Title is required.",
            minlength: "Title must be at least 3 characters long."
        },
        isActive: {
            required: "Please select a status (Active or Inactive)."
        },
        image_name: {
            extension: "Only jpg, jpeg, or png files are allowed."
        }

    });

    // Password Form (Special Validation for Passwords)
    $('#password-form').submit(function (e) {
        e.preventDefault(); // Prevent form submission if validation fails

        var currentPassword = $("input[name='currentPassword']").val();
        var newPassword = $("input[name='newPassword']").val();
        var confirmPassword = $("input[name='confirmPassword']").val();

        $(".error").remove(); // Remove previous error messages

        if (currentPassword === "" || newPassword === "" || confirmPassword === "") {
            $('#password-form').prepend('<p class="error" style="color: red;">All fields are required.</p>');
            return;
        }

        if (newPassword !== confirmPassword) {
            $('#password-form').prepend('<p class="error" style="color: red;">Passwords do not match.</p>');
            return;
        }

        this.submit(); // If validation passes, submit the form
    });

    // Enhanced Validation for Order Form
    function validateOrderForm() {
        // Email Regex
        var emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
        // Mobile Regex (10 digits)
        var mobileRegex = /^[0-9]{10}$/;

        var firstName = $("input[name='firstName']").val();
        var lastName = $("input[name='lastName']").val();
        var email = $("input[name='email']").val();
        var mobileNo = $("input[name='mobileNo']").val();
        var address = $("input[name='address']").val();
        var city = $("input[name='city']").val();
        var pincode = $("input[name='pincode']").val();
        var paymentType = $("#paymentType").val();

        var formValid = true;

        // Reset previous error messages
        $(".error").remove();
        $("input").removeClass('is-invalid');

        if (firstName === "") {
            $('#orderForm').prepend('<p class="error" style="color: red;">First Name is required.</p>');
            $("input[name='firstName']").addClass('is-invalid');
            formValid = false;
        }
        else if (email === "" || !emailRegex.test(email)) {
            $('#orderForm').prepend('<p class="error" style="color: red;">Please enter a valid email address.</p>');
            $("input[name='email']").addClass('is-invalid');
            formValid = false;
        }
        else if (mobileNo === "" || !mobileRegex.test(mobileNo)) {
            $('#orderForm').prepend('<p class="error" style="color: red;">Please enter a valid mobile number.</p>');
            $("input[name='mobileNo']").addClass('is-invalid');
            formValid = false;
        }
        else if (address === "") {
            $('#orderForm').prepend('<p class="error" style="color: red;">Address is required.</p>');
            $("input[name='address']").addClass('is-invalid');
            formValid = false;
        }
        else if (city === "") {
            $('#orderForm').prepend('<p class="error" style="color: red;">City is required.</p>');
            $("input[name='city']").addClass('is-invalid');
            formValid = false;
        }
        else if (pincode === "") {
            $('#orderForm').prepend('<p class="error" style="color: red;">Pincode is required.</p>');
            $("input[name='pincode']").addClass('is-invalid');
            formValid = false;
        }
        else if (paymentType === "") {
            $('#orderForm').prepend('<p class="error" style="color: red;">Please select a payment method.</p>');
            $("#paymentType").addClass('is-invalid');
            formValid = false;
        }

        if (formValid) {
            return true;
        } else {
            return false;
        }
    }

    // Order Form Validation
    $('#orderForm').submit(function (e) {
        if (!validateOrderForm()) {
            e.preventDefault(); // Prevent form submission if validation fails
        }
    });
});
