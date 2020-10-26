package com.thewear.thewearapp;
import android.util.Patterns;
import android.widget.EditText;
import org.junit.Test;
import org.junit.runner.RunWith;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest extends ActivityTestRule<LoginActivity> {
    LoginActivity activity;
    EditText inputEmail;

    public LoginActivityTest() {
        super(LoginActivity.class);
    }

    @Override
    public LoginActivity getActivity() {
        return activity;
    }

  public void init(){
      inputEmail = (EditText)getActivity().findViewById(R.id.login_email);
  }

    @Test
    public void onCreate() {
        Boolean expected= Patterns.EMAIL_ADDRESS.matcher("name@g.com").matches();
        inputEmail.setText("tester@g.com");
        Boolean actual =Patterns.EMAIL_ADDRESS.matcher(inputEmail.getText().toString()).matches();

        assertEquals("Email format:",expected, actual);
    }

}