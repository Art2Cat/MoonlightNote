package com.art2cat.dev.moonlightnote.controller.settings;

import java.io.Serializable;

/**
 * SettingsTypeEnum for
 *
 * @see CommonSettingsFragment
 */

public enum SettingsTypeEnum implements Serializable {

  /**
   * @see PrivacyPolicyFragment
   */
  POLICY,
  /**
   * @see AboutAppFragment
   */
  ABOUT,
  /**
   * @see LicenseFragment
   */
  LICENSE,
  /**
   * unused
   */
  SECURITY;
}
