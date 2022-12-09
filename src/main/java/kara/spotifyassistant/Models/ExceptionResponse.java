package kara.spotifyassistant.Models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@Getter @Setter
public class ExceptionResponse {
  private String message;
  private int status;
  private Date timestamp;
  private String path;
}
