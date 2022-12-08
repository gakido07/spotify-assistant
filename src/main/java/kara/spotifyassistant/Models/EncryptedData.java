package kara.spotifyassistant.Models;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class EncryptedData {
  private String iv;
  private String data;
}
