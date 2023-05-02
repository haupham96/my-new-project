package com.example.research.entity;

import java.lang.Boolean;
import java.lang.Integer;
import java.lang.String;
import java.sql.Timestamp;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecyclerBranch {
  /**
   * "UUID."
   */
  private UUID uuid;

  /**
   * "ユーザID."
   */
  private String userId;

  /**
   * "処理業者ID."
   */
  private String recyclerId;

  /**
   * "事業所ID."
   */
  private String branchId;

  /**
   * "シーケンスID."
   */
  private Integer sequenceId;

  /**
   * "ファイル名."
   */
  private String fileName;

  /**
   * "ファイルサイズ."
   */
  private String fileSize;

  /**
   * "ファイルパス."
   */
  private String filePath;

  /**
   * "登録日時."
   */
  private Timestamp createTimestamp;

  /**
   * "登録者ID."
   */
  private String createUserId;

  /**
   * "更新日時."
   */
  private Timestamp updateTimestamp;

  /**
   * "更新者ID."
   */
  private String updateUserId;

  /**
   * "削除日時."
   */
  private Timestamp deleteTimestamp;

  /**
   * "削除者ID."
   */
  private String deleteUserId;

  /**
   * "状態."
   */
  private Boolean isActive;
}
